package com.francescoalessi.hackernews.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CommentsViewModelFactory extends ViewModelProvider.NewInstanceFactory
{

    private final Application application;
    private final int storyId;

    public CommentsViewModelFactory(Application application, int storyId)
    {
        this.application = application;
        this.storyId = storyId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        return (T) new CommentsViewModel(application, storyId);
    }
}