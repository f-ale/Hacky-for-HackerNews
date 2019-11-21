package com.francescoalessi.hackernews;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
    Item story;

    RecyclerView mCommentsRecyclerView;
    CommentsAdapter mCommentsAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    RequestQueue queue;
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

        queue = Volley.newRequestQueue(this);

        CommentsViewModelFactory factory = new CommentsViewModelFactory(getApplication(), storyId);
        mViewModel = ViewModelProviders.of(this, factory).get(CommentsViewModel.class);
        mViewModel.getComments().observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments)
            {
                mCommentsAdapter.setComments(comments);
            }
        });

        if(savedInstanceState == null)
        {
            swipeRefreshLayout.setRefreshing(true);
            fetchComments();
        }

        mViewModel.getStoryTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String title)
            {
                setTitle(title);
            }
        });

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

    private void fetchComments()
    {
        String url = "https://api.hackerwebapp.com/item/" + storyId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        story = Item.parseNodeApiItem(response);
                        setTitle(story.title);
                        ArrayList<Item> comments = new ArrayList<>();
                        populateComments(story, story.children, comments);
                        for(int i = 0; i < comments.size(); i++)
                        {
                            mViewModel.insert(Comment.parseItem(comments.get(i), i));
                        }
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

    @Override
    public void onRefresh()
    {
        fetchComments();
    }
}
