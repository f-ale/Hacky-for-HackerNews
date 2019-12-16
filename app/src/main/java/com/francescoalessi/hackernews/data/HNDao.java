package com.francescoalessi.hackernews.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HNDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Story story);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment comment);

    @Query("SELECT * FROM comments WHERE story_id = :storyId ORDER BY place ASC")
    LiveData<List<Comment>> getCommentsForStory(int storyId);

    @Query("DELETE FROM comments WHERE story_id = :storyId")
    void deleteCommentsForStory(int storyId);

    @Query("DELETE FROM comments WHERE story_id NOT IN (SELECT id FROM stories)")
    int deleteParentlessComments();

    @Query("SELECT * FROM stories ORDER BY place ASC LIMIT :amount")
    LiveData<List<Story>> getTopStories(int amount);

    @Query("DELETE FROM stories WHERE place = :place AND id != :id")
    void deleteStoryWithPlaceIfDifferent(int place, int id);

    @Query("SELECT * FROM stories WHERE id = :storyId")
    LiveData<Story> getStory(int storyId);
}
