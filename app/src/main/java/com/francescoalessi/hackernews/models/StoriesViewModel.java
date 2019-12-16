package com.francescoalessi.hackernews.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.francescoalessi.hackernews.data.HNRepository;
import com.francescoalessi.hackernews.data.Story;

import java.util.List;

public class StoriesViewModel extends AndroidViewModel
{
    private HNRepository mRepository;
    private LiveData<List<Story>> mTopStories;
    private int loadedStories;

    public StoriesViewModel(Application application) {
        super(application);
        mRepository = HNRepository.getInstance(application);
        loadedStories = 20;
        mTopStories = mRepository.getTopStories(loadedStories);
    }
    public LiveData<Boolean> isRefreshing() { return mRepository.getIsRefreshingStories(); }

    public LiveData<List<Story>> getTopStories() { return mTopStories; }

    public LiveData<List<Story>> loadMoreStories()
    {
        if(loadedStories < 500-20)
        {
            loadedStories += 20;
            mTopStories = mRepository.getTopStories(loadedStories);
            return mTopStories;
        }
        else
            return mTopStories;
    }

    public void refreshStories()
    {
        mRepository.refreshStories(loadedStories);
    }
}