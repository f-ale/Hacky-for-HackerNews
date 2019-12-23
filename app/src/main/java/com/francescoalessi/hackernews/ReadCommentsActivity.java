package com.francescoalessi.hackernews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.francescoalessi.hackernews.adapters.CommentsAdapter;
import com.francescoalessi.hackernews.data.Comment;
import com.francescoalessi.hackernews.data.Story;
import com.francescoalessi.hackernews.models.CommentsViewModel;
import com.francescoalessi.hackernews.models.CommentsViewModelFactory;

import java.util.List;

public class ReadCommentsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private int storyId = -1;
    private CommentsAdapter mCommentsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommentsViewModel mViewModel;
    private Story mStory;

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

        RecyclerView commentsRecyclerView = findViewById(R.id.rv_comments);
        mCommentsAdapter = new CommentsAdapter(this);
        commentsRecyclerView.setAdapter(mCommentsAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                if(story.text != null && !story.text.equals(""))
                    mCommentsAdapter.setStory(story);

                mStory = story;

                storyLivedata.removeObserver(this);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_comments_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.action_share)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mStory.title + "\n" + "https://news.ycombinator.com/item?id=" + mStory.id);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, mStory.title);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "Share: " + mStory.title);
            startActivity(shareIntent);
        }

        if(item.getItemId() == R.id.action_view_url)
        {
            Intent viewIntent = new Intent();
            viewIntent.setAction(Intent.ACTION_VIEW);
            viewIntent.setData(Uri.parse(mStory.url));
            startActivity(viewIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh()
    {
        mViewModel.refreshComments();
    }
}
