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

import java.util.List;

public class HNRepository
{
    private HNDao dao;
    private RequestQueue queue;
    private int[] topStoriesIDs = new int[500];
    private MutableLiveData<Boolean> isRefreshing;

    public HNRepository(Application application)
    {
        HNRoomDatabase db = HNRoomDatabase.getDatabase(application);
        dao = db.dao();
        queue = Volley.newRequestQueue(application);

        isRefreshing = new MutableLiveData<>(false);
        getStories(20);
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
                fetchStories(0, amount);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("RepositoryFetching", error.toString());
                isRefreshing.postValue(false);
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void fetchStories(int start, int end)
    {
        isRefreshing.setValue(true);

        for(int i = start; i < end; i++)
        {
            int item = topStoriesIDs[i];
            final boolean isLastRequest = i == end-1;

            Log.d("RepositoryFetching", item + "");
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
                                isRefreshing.postValue(false);
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // TODO: Handle error
                            Log.d("RepositoryFetching", error.toString());
                            isRefreshing.postValue(false);
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public LiveData<List<Story>> getTopStories(int amount)
    {
        fetchStories(0, amount);
        return dao.getTopStories(amount);
    }

    public void refreshStories(int amount)
    {
        getStories(amount);
    }

    public LiveData<Boolean> getIsRefreshing()
    {
        return isRefreshing;
    }

    public LiveData<List<Comment>> getCommentsForStory(int storyId) { return dao.getCommentsForStory(storyId); }

    public LiveData<String> getStoryTitle(int storyId) { return dao.getStoryTitle(storyId); }

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

    public void insert(final Comment comment)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                dao.insert(comment);
            }
        });
    }

    public void deleteCommentsForStory(final int storyId)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run()
            {
                dao.deleteCommentsForStory(storyId);
            }
        });
    }

}
