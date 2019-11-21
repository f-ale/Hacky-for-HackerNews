package com.francescoalessi.hackernews.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.hackernews.MainActivity;
import com.francescoalessi.hackernews.R;
import com.francescoalessi.hackernews.ReadCommentsActivity;
import com.francescoalessi.hackernews.data.Item;
import com.francescoalessi.hackernews.data.Story;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesViewHolder>
{
    private List<Story> mStoriesArray;
    private LayoutInflater mInflater;

    public StoriesAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = mInflater.inflate(R.layout.thread_item, parent, false);
        return new StoriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position)
    {
        if (mStoriesArray != null)
        {
            Story mCurrent = mStoriesArray.get(position);
            holder.mTitleTextView.setText(mCurrent.title);
            holder.mUpvotesTextView.setText(mCurrent.score + " points");
            holder.mCommentsButton.setText(mCurrent.comments + "");
            String fullUrl = mCurrent.url;
            try
            {
                URI url = new URI(fullUrl);
                holder.mDomainTextView.setText(url.getHost());
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount()
    {
        if (mStoriesArray != null)
        {
            return mStoriesArray.size();
        }
        else
            return 0;
    }

    public void setStories(List<Story> stories)
    {
        mStoriesArray = stories;
        notifyDataSetChanged();
    }

    class StoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mTitleTextView;
        TextView mDomainTextView;
        Button mCommentsButton;
        TextView mUpvotesTextView;

        StoriesViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.tv_post_title);
            mDomainTextView = itemView.findViewById(R.id.tv_post_domain);
            mCommentsButton = itemView.findViewById(R.id.btn_post_comments);
            mCommentsButton.setOnClickListener(this);
            mUpvotesTextView = itemView.findViewById(R.id.tv_post_upvotes);

            View.OnClickListener listener = new StoryOnClickListener();
            itemView.setOnClickListener(listener);
        }

        @Override
        public void onClick(View view)
        {
            Context context = view.getContext();
            int position = getLayoutPosition();
            if(mStoriesArray.get(position).comments > 0)
            {
                // Launch story view activity
                Intent intent = new Intent(context, ReadCommentsActivity.class);
                intent.putExtra(MainActivity.STORY_ID, mStoriesArray.get(position).id);
                context.startActivity(intent);
            }
        }

        class StoryOnClickListener implements View.OnClickListener
        {
            @Override
            public void onClick(View view)
            {
                int position = getLayoutPosition();
                Context context = view.getContext();
                // Get story URL
                String url = mStoriesArray.get(position).url;
                // Launch view intent for story URL
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        }
    }
}
