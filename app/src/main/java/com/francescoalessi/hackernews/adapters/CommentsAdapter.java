package com.francescoalessi.hackernews.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.francescoalessi.hackernews.R;
import com.francescoalessi.hackernews.data.Comment;
import com.francescoalessi.hackernews.data.Story;
import com.francescoalessi.hackernews.utils.Utils;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.StoriesViewHolder>
{
    private Story mStory;
    private List<Comment> mComments;
    private final LayoutInflater mInflater;
    private final Context context;

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

    private void setTextViews(StoriesViewHolder holder, String author, String content)
    {
        holder.mUserTextView.setText(author);
        holder.mContentTextView.setText(content);
        Linkify.addLinks(holder.mContentTextView, Linkify.WEB_URLS);
        holder.mContentTextView.setText(Utils.trimTrailingWhitespace(Html.fromHtml(holder.mContentTextView.getText().toString())));
    }

    private void setTimeAgo(StoriesViewHolder holder, long time)
    {
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(time*1000);
        holder.mTimeAgoTextView.setText(timeAgo);
    }

    private void setViewsVisibility(StoriesViewHolder holder, int visibility)
    {
        holder.mConstraintLayout.setVisibility(visibility);
        holder.mContentTextView.setVisibility(visibility);
        holder.mTimeAgoTextView.setVisibility(visibility);
        holder.mUserTextView.setVisibility(visibility);
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position)
    {
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) holder.mConstraintLayout.getLayoutParams();
        float density = context.getResources().getDisplayMetrics().density;

        if(position == 0 && mStory != null)
        {
            holder.mConstraintLayout.setVisibility(View.VISIBLE);
            holder.mContentTextView.setVisibility(View.VISIBLE);
            holder.mTimeAgoTextView.setVisibility(View.INVISIBLE);
            holder.mUserTextView.setVisibility(View.VISIBLE);
            holder.mCommentColor.setImageDrawable(null);
            holder.mCollapsedImageView.setVisibility(View.INVISIBLE);

            setTextViews(holder, mStory.author, mStory.text);
            marginParams.setMargins(0, marginParams.topMargin, marginParams.rightMargin, Math.round(1*density));
            return;
        }
        else
        {
            if(mStory != null)
                position--;
        }

        if (mComments != null)
        {
            Comment mCurrent = mComments.get(position);

            if(mCurrent.collapsed)
            {
                setViewsVisibility(holder, View.GONE);

                marginParams.setMargins(marginParams.leftMargin, marginParams.topMargin, marginParams.rightMargin, 0);
            }
            else
            {
                setViewsVisibility(holder, View.VISIBLE);

                marginParams.setMargins(Math.round(8*density*mCurrent.level), marginParams.topMargin, marginParams.rightMargin, Math.round(1*density));

                setTextViews(holder, mCurrent.author, mCurrent.text);
                setTimeAgo(holder, mCurrent.time);

                if(mCurrent.level != 0)
                    holder.mCommentColor.setImageDrawable(new ColorDrawable(stringToColor(mCurrent.author)));
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

    public void setStory(Story story)
    {
        mStory = story;
        notifyDataSetChanged();
    }

    class StoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView mUserTextView;
        final TextView mContentTextView;
        final TextView mTimeAgoTextView;
        final ImageView mCommentColor;
        final ImageView mCollapsedImageView;
        final ConstraintLayout mConstraintLayout;

        StoriesViewHolder(@NonNull final View itemView)
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
            mContentTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view)
                {
                    String text = mUserTextView.getText() + "\n\n" + mContentTextView.getText();
                    ClipData comment = ClipData.newPlainText("HN Comment", text);
                    ClipboardManager clipboard = (ClipboardManager)
                            context.getSystemService(Context.CLIPBOARD_SERVICE);

                    if(clipboard != null)
                    {
                        clipboard.setPrimaryClip(comment);
                        Toast.makeText(context, "Comment copied", Toast.LENGTH_SHORT).show();
                        view.playSoundEffect(SoundEffectConstants.CLICK);
                        return true;
                    }

                    return false;
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            if(view instanceof TextView)
            {
                TextView textView = (TextView) view;
                //textView.setTextIsSelectable(false);
                if(!(textView.getSelectionEnd() == -1 && textView.getSelectionStart() == -1))
                    return;
            }

            int position = getLayoutPosition()+1;
            if(mStory != null)
                position--;

            if(position > 0)
            {
                Comment thisComment = mComments.get(position-1);
                thisComment.childrenCollapsed = !thisComment.childrenCollapsed;

                int counter = 0;
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
                            counter++;
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

                if(counter == 0)
                    thisComment.childrenCollapsed = false;

                if(thisComment.childrenCollapsed)
                {
                    mCollapsedImageView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mCollapsedImageView.setVisibility(View.GONE);
                }
            }

        }
    }

    private int stringToColor(String string)
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
