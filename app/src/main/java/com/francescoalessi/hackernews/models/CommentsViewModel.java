package com.francescoalessi.hackernews.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.francescoalessi.hackernews.data.Comment;
import com.francescoalessi.hackernews.data.HNRepository;
import com.francescoalessi.hackernews.data.Story;

import java.util.List;

public class CommentsViewModel extends AndroidViewModel
{
    private HNRepository mRepository;
    private LiveData<List<Comment>> mComments;
    private LiveData<Story> mStory;
    private int storyId;

    CommentsViewModel(Application application, int storyId) {
        super(application);
        mRepository = HNRepository.getInstance(application);
        mComments = mRepository.getCommentsForStory(storyId);
        mStory = mRepository.getStory(storyId);
        this.storyId = storyId;
    }

    public LiveData<List<Comment>> getComments() { return mComments; }
    public LiveData<Story> getStory() { return mStory; }
    public LiveData<Boolean> isRefreshing() { return mRepository.getIsRefreshingComments(); }
    public void refreshComments() { mRepository.refreshComments(storyId);}
}