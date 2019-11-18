package com.francescoalessi.hackernews.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.hackernews.MainActivity;
import com.francescoalessi.hackernews.R;
import com.francescoalessi.hackernews.ReadCommentsActivity;
import com.francescoalessi.hackernews.data.Item;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.StoriesViewHolder>
{
    private ArrayList<Item> mCommentsArray;
    private LayoutInflater mInflater;
    private Context context;

    public CommentsAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = mInflater.inflate(R.layout.comment_item, parent, false);
        return new StoriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position)
    {
        if (mCommentsArray != null)
        {
            Item mCurrent = mCommentsArray.get(position);
            holder.mUserTextView.setText(mCurrent.by);
            holder.mContentTextView.setText(Html.fromHtml(mCurrent.text));
            holder.mTimeAgoTextView.setText(mCurrent.timeAgo);
            float density = context.getResources().getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) holder.mConstraintLayout.getLayoutParams();
            marginParams.setMargins(Math.round(8*density*mCurrent.level), marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);

            if(mCurrent.level != 0)
                holder.mCommentColor.setImageDrawable(new ColorDrawable(stringToColor(mCurrent.by, mCurrent.level)));
            else
                holder.mCommentColor.setImageDrawable(null);
            //holder.mCommentCardView.setCardElevation(holder.mCommentCardView.getCardElevation() - 1 * mCurrent.level * density);
        }
    }

    @Override
    public int getItemCount()
    {
        if (mCommentsArray != null)
        {
            return mCommentsArray.size();
        }
        else
            return 0;
    }

    public void setComments(ArrayList<Item> items)
    {
        mCommentsArray = items;
        notifyDataSetChanged();
    }

    class StoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mUserTextView;
        TextView mContentTextView;
        TextView mTimeAgoTextView;
        ImageView mCommentColor;
        ConstraintLayout mConstraintLayout;
        boolean mIsExpanded = true;

        StoriesViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mUserTextView = itemView.findViewById(R.id.tv_comment_author);
            mContentTextView = itemView.findViewById(R.id.tv_comment_text);
            mTimeAgoTextView = itemView.findViewById(R.id.tv_time_ago);
            mCommentColor = itemView.findViewById(R.id.iv_comment_color);
            mConstraintLayout = itemView.findViewById(R.id.comment_layout);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            mIsExpanded = !mIsExpanded;
        }
    }

    private int stringToColor(String string, int level)
    {
        int hash = string.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        float[] hsv = new float[3];

        Color.RGBToHSV(r,g,b,hsv);
        //hsv[0] = hash%359;
        hsv[2] = (float) (0.8);
        hsv[1] = (float) (0.9);
        return Color.HSVToColor(hsv);
    }
}
