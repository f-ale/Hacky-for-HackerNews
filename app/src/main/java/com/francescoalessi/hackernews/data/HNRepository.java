package com.francescoalessi.hackernews.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.francescoalessi.hackernews.concurrency.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HNRepository
{
    private static HNRepository instance;
    private final HNDao dao;
    private final RequestQueue queue;
    private final int[] topStoriesIDs = new int[500];
    private final MutableLiveData<Boolean> isRefreshingStories;
    private final MutableLiveData<Boolean> isRefreshingComments;

    private HNRepository(Application application)
    {
        HNRoomDatabase db = HNRoomDatabase.getDatabase(application);
        dao = db.dao();
        queue = Volley.newRequestQueue(application);

        isRefreshingStories = new MutableLiveData<>(false);
        isRefreshingComments = new MutableLiveData<>(false);
        getStories(20);
    }

    public static HNRepository getInstance(Application application)
    {
        if(instance == null)
        {
            instance = new HNRepository(application);
        }

        return instance;
    }

    private void populateComments(Item story, JSONArray comments, ArrayList<Item> commentsList)
    {
        try
        {
            for (int i = 0; i < comments.length(); i++)
            {
                Item item = Item.parseNodeApiItem(comments.getJSONObject(i));
                item.storyId = story.id;
                commentsList.add(item);
                populateComments(story, item.children, commentsList);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    private void fetchCommentsForStory(int storyId)
    {
        String url = "https://api.hackerwebapp.com/item/" + storyId;
        isRefreshingComments.setValue(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        final Item story = Item.parseNodeApiItem(response);
                        final ArrayList<Item> comments = new ArrayList<>();

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run()
                            {
                                dao.deleteCommentsForStory(story.id);
                                populateComments(story, story.children, comments);
                                for(int i = 0; i < comments.size(); i++)
                                {
                                    dao.insert(Comment.parseItem(comments.get(i), i));
                                }
                                isRefreshingComments.postValue(false);
                            }
                        });
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("FETCH_STORY_COMMENTS", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void getStories(final int amount)
    {
        String url = "https://hacker-news.firebaseio.com/v0/topstories.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                for (int i = 0; i < response.length(); i++)
                {
                    try
                    {
                        topStoriesIDs[i] = response.getInt(i);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                deleteParentlessComments();
                fetchStories(0, amount);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("GET_STORIES", error.toString());
                isRefreshingStories.postValue(false);
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void fetchStories(int start, int end)
    {
        isRefreshingStories.setValue(true);

        for(int i = start; i < end; i++)
        {
            int item = topStoriesIDs[i];
            final boolean isLastRequest = i == end-1;

            String url = "https://hacker-news.firebaseio.com/v0/item/" + item + ".json";
            final int finalI = i;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            insert(Story.parse(response, finalI));
                            if(isLastRequest)
                            {
                                isRefreshingStories.postValue(false);
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.d("FETCH_STORIES", error.toString());
                            isRefreshingStories.postValue(false);
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    private void deleteParentlessComments()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run()
            {
                int i = dao.deleteParentlessComments();
            }
        });
    }

    public LiveData<List<Story>> getTopStories(int amount)
    {
        fetchStories(amount-20, amount);
        return dao.getTopStories(amount);
    }

    public void refreshStories(int amount)
    {
        getStories(amount);
    }

    public void refreshComments(int storyId) { fetchCommentsForStory(storyId); }

    public LiveData<Boolean> getIsRefreshingStories()
    {
        return isRefreshingStories;
    }
    public LiveData<Boolean> getIsRefreshingComments()
    {
        return isRefreshingComments;
    }

    public LiveData<List<Comment>> getCommentsForStory(int storyId)
    {
        fetchCommentsForStory(storyId);
        return dao.getCommentsForStory(storyId);
    }

    public LiveData<Story> getStory(int storyId) { return dao.getStory(storyId); }

    private void insert(final Story story)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                dao.deleteStoryWithPlaceIfDifferent(story.place, story.id);
                dao.insert(story);
            }
        });
    }

}
