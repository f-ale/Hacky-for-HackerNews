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
    private LiveData<String> mStoryTitle;
    private int storyId;

    CommentsViewModel(Application application, int storyId) {
        super(application);
        mRepository = new HNRepository(application);
        mComments = mRepository.getCommentsForStory(storyId);
        mStoryTitle = mRepository.getStoryTitle(storyId);
        this.storyId = storyId;
    }

    public void insert(Comment comment) { mRepository.insert(comment); }
    public LiveData<List<Comment>> getComments() { return mComments; }
    public LiveData<String> getStoryTitle() { return mStoryTitle; }
    public void deleteAllComments() { mRepository.deleteCommentsForStory(storyId);}
}