package com.francescoalessi.hackernews;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.francescoalessi.hackernews.adapters.StoriesAdapter;
import com.francescoalessi.hackernews.data.Story;
import com.francescoalessi.hackernews.models.StoriesViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    LiveData<List<Story>> topStories;
    int[] topItems = new int[500];

    RecyclerView mStoriesRecyclerView;
    StoriesAdapter mAdapter;
    RequestQueue queue;
    SwipeRefreshLayout swipeRefreshLayout;
    StoriesViewModel mViewModel;

    public static final String STORY_ID = "story_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(StoriesViewModel.class);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        mStoriesRecyclerView = findViewById(R.id.rv_stories);
        mAdapter = new StoriesAdapter(this);
        mStoriesRecyclerView.setAdapter(mAdapter);
        mStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        queue = Volley.newRequestQueue(this);

        topStories = mViewModel.getTopStories();
        topStories.observe(this, new Observer<List<Story>>() {
            @Override
            public void onChanged(List<Story> stories)
            {
                mAdapter.setStories(stories);
            }
        });

        if(savedInstanceState == null)
        {
            swipeRefreshLayout.setRefreshing(true);
            getStories();
        }
    }

    private void getStories()
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
                        topItems[i] = response.getInt(i);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                fetchStories(queue);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("HERE", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void fetchStories(RequestQueue queue)
    {
        for(int i = 0; i < 200; i++)
        {
            int item = topItems[i];

            Log.d("HERE", item + "");
            String url = "https://hacker-news.firebaseio.com/v0/item/" + item + ".json";
            final int finalI = i;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                    {

                        @Override
                        public void onResponse(JSONObject response)
                        {
                            mViewModel.insert(Story.parse(response, finalI));
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // TODO: Handle error
                            Log.d("HERE", error.toString());
                        }
                    });

            queue.add(jsonObjectRequest);
        }
    }

    @Override
    public void onRefresh()
    {
        topItems = new int[500];
        getStories();
    }
}
