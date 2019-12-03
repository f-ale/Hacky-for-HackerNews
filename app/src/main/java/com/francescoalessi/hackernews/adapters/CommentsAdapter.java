package com.francescoalessi.hackernews.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.hackernews.R;
import com.francescoalessi.hackernews.data.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.StoriesViewHolder>
{
    private List<Comment> mComments;
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
        if (mComments != null)
        {
            Comment mCurrent = mComments.get(position);
            float density = context.getResources().getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) holder.mConstraintLayout.getLayoutParams();

            if(mCurrent.collapsed)
            {
                holder.mConstraintLayout.setVisibility(View.GONE);
                holder.mContentTextView.setVisibility(View.GONE);
                holder.mTimeAgoTextView.setVisibility(View.GONE);
                holder.mUserTextView.setVisibility(View.GONE);
                marginParams.setMargins(marginParams.leftMargin, marginParams.topMargin, marginParams.rightMargin, 0);
            }
            else
            {
                holder.mConstraintLayout.setVisibility(View.VISIBLE);
                holder.mContentTextView.setVisibility(View.VISIBLE);
                holder.mTimeAgoTextView.setVisibility(View.VISIBLE);
                holder.mUserTextView.setVisibility(View.VISIBLE);

                holder.mUserTextView.setText(mCurrent.author);
                holder.mContentTextView.setText(Html.fromHtml(mCurrent.text));
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(mCurrent.time*1000);
                holder.mTimeAgoTextView.setText(timeAgo);
                marginParams.setMargins(Math.round(8*density*mCurrent.level), marginParams.topMargin, marginParams.rightMargin, Math.round(1*density));

                if(mCurrent.level != 0)
                    holder.mCommentColor.setImageDrawable(new ColorDrawable(stringToColor(mCurrent.author, mCurrent.level)));
                else
                    holder.mCommentColor.setImageDrawable(null);
            }
            if(mCurrent.childrenCollapsed)
            {
                holder.mCollapsedImageView.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.mCollapsedImageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        if (mComments != null)
        {
            return mComments.size();
        }
        else
            return 0;
    }

    public void setComments(List<Comment> comments)
    {
        mComments = comments;
        notifyDataSetChanged();
    }

    class StoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mUserTextView;
        TextView mContentTextView;
        TextView mTimeAgoTextView;
        ImageView mCommentColor;
        ImageView mCollapsedImageView;
        ConstraintLayout mConstraintLayout;

        StoriesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mUserTextView = itemView.findViewById(R.id.tv_comment_author);
            mContentTextView = itemView.findViewById(R.id.tv_comment_text);
            mTimeAgoTextView = itemView.findViewById(R.id.tv_time_ago);
            mCommentColor = itemView.findViewById(R.id.iv_comment_color);
            mConstraintLayout = itemView.findViewById(R.id.comment_layout);
            mCollapsedImageView = itemView.findViewById(R.id.iv_collapsed);
            itemView.setOnClickListener(this);
            mContentTextView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            if(view instanceof TextView)
            {
                TextView textView = (TextView) view;
                if(!(textView.getSelectionEnd() == -1 && textView.getSelectionStart() == -1))
                    return;
            }
            int position = getLayoutPosition()+1;
            Comment thisComment = mComments.get(getLayoutPosition());
            thisComment.childrenCollapsed = !thisComment.childrenCollapsed;

            if(thisComment.childrenCollapsed)
            {
                mCollapsedImageView.setVisibility(View.VISIBLE);
            }
            else
            {
                mCollapsedImageView.setVisibility(View.GONE);
            }

            while(position < mComments.size())
            {
                Comment comment = mComments.get(position);
                Log.d("COLLAPSE", comment.author + ": " + comment.level + ", " + thisComment.author + ": " + thisComment.level);
                if(comment.level <= thisComment.level)
                    break;
                else
                {
                    if(thisComment.childrenCollapsed)
                    {
                        comment.collapsed = true;
                    }
                    else
                    {
                        comment.collapsed = false;
                    }
                    comment.childrenCollapsed = false;

                    notifyItemChanged(position);
                }
                position++;
            }
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
