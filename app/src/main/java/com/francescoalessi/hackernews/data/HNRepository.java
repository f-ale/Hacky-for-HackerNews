package com.francescoalessi.hackernews.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.francescoalessi.hackernews.concurrency.AppExecutors;
import java.util.List;

public class HNRepository
{
    private HNDao dao;
    private LiveData<List<Story>> topStories;

    public HNRepository(Application application)
    {
        HNRoomDatabase db = HNRoomDatabase.getDatabase(application);
        dao = db.dao();
        topStories = dao.getTopStories(200);
    }

    public LiveData<List<Story>> getTopStories()
    {
        return topStories;
    }

    public LiveData<List<Comment>> getCommentsForStory(int storyId) { return dao.getCommentsForStory(storyId); }

    public LiveData<String> getStoryTitle(int storyId) { return dao.getStoryTitle(storyId); }

    public void insert(final Story story)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                dao.insert(story);
            }
        });
    }

    public void insert(final Comment comment)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable()
        {
            @Override
            public void run()
            {
                dao.insert(comment);
            }
        });
    }

}
