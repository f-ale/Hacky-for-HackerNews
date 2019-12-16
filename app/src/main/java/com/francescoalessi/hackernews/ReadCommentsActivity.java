package com.francescoalessi.hackernews;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.francescoalessi.hackernews.adapters.CommentsAdapter;
import com.francescoalessi.hackernews.data.Comment;
import com.francescoalessi.hackernews.data.Item;
import com.francescoalessi.hackernews.data.Story;
import com.francescoalessi.hackernews.models.CommentsViewModel;
import com.francescoalessi.hackernews.models.CommentsViewModelFactory;
import com.francescoalessi.hackernews.models.StoriesViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadCommentsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    int storyId = -1;

    RecyclerView mCommentsRecyclerView;
    CommentsAdapter mCommentsAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    CommentsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_comments);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        mCommentsRecyclerView = findViewById(R.id.rv_comments);
        mCommentsAdapter = new CommentsAdapter(this);
        mCommentsRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null)
        {
            storyId = intent.getIntExtra(MainActivity.STORY_ID, -1);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        CommentsViewModelFactory factory = new CommentsViewModelFactory(getApplication(), storyId);
        mViewModel = ViewModelProviders.of(this, factory).get(CommentsViewModel.class);
        mViewModel.getComments().observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments)
            {
                Log.d("SETTING COMMENTS", storyId + ", " + comments.toString());
                mCommentsAdapter.setComments(comments);
            }
        });

        mViewModel.isRefreshing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRefreshing)
            {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });

        final LiveData<Story> storyLivedata = mViewModel.getStory();
        storyLivedata.observe(this, new Observer<Story>() {
            @Override
            public void onChanged(Story story)
            {
                setTitle(story.title);
                storyLivedata.removeObserver(this);
            }
        });

    }

    @Override
    public void onRefresh()
    {
        mViewModel.refreshComments();
    }
}
