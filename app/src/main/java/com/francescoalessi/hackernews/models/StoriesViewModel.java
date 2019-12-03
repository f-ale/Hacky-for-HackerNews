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
        mRepository = new HNRepository(application);
        loadedStories = 20;
        mTopStories = mRepository.getTopStories(loadedStories);
    }

    public LiveData<List<Story>> getTopStories() { return mTopStories; }

    public int loadMoreStories()
    {
        if(loadedStories < 500-20)
        {
            loadedStories += 20;
            mTopStories = mRepository.getTopStories(loadedStories);
            return loadedStories;
        }
        else
            return -1;
    }

    public int getLoadedStoriesAmount()
    {
        return loadedStories;
    }

    public void insert(Story story)
    {
        mRepository.deleteStoryWithPlace(story.place, story.id);
        mRepository.insert(story);
    }
}