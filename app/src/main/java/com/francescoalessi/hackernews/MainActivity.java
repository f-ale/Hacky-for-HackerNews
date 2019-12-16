package com.francescoalessi.hackernews;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
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

        mViewModel.isRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRefreshing)
            {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });

        mStoriesRecyclerView = findViewById(R.id.rv_stories);
        mAdapter = new StoriesAdapter(this);
        mStoriesRecyclerView.setAdapter(mAdapter);
        mStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        queue = Volley.newRequestQueue(this);

        mStoriesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());

                if (layoutManager != null && (layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1) && !swipeRefreshLayout.isRefreshing()) {

                    //swipeRefreshLayout.setRefreshing(true);
                    topStories.removeObservers((AppCompatActivity) recyclerView.getContext());
                    topStories = mViewModel.loadMoreStories();
                    topStories.observe((AppCompatActivity) recyclerView.getContext(), new Observer<List<Story>>() {
                        @Override
                        public void onChanged(List<Story> stories)
                        {
                            mAdapter.setStories(stories);
                        }
                    });
                }
            }
        });

        topStories = mViewModel.getTopStories();
        topStories.observe(this, new Observer<List<Story>>() {
            @Override
            public void onChanged(List<Story> stories)
            {
                mAdapter.setStories(stories);
            }
        });
    }

    @Override
    public void onRefresh()
    {
        topItems = new int[500];
        mViewModel.refreshStories();
    }
}
