package com.francescoalessi.hackernews.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "comments", foreignKeys =
        @ForeignKey(entity = Story.class,
        parentColumns = "id",
        childColumns = "story_id",
        onDelete = CASCADE)
)

public class Comment
{
    @PrimaryKey
    public int id;

    @ColumnInfo(name="story_id")
    public long storyId;
    @NonNull
    public String author;
    @NonNull
    public String text;
    public int place;
    public int level;
    public long time;

    @Ignore
    public boolean collapsed = false;
    @Ignore
    public boolean childrenCollapsed = false;

    @Ignore
    public static Comment parse(JSONObject object, int place, int storyId)
    {
        Comment comment = new Comment();
        try
        {
            comment.id = object.getInt("id");
            comment.text = object.optString("content", "");
            comment.author = object.optString("user");
            comment.time = object.optLong("time");
            comment.level = object.optInt("level");
            comment.place = place;
            comment.storyId = storyId;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        return comment;
    }

    @Ignore
    public static Comment parseItem(Item object, int place)
    {
        Comment comment = new Comment();

            comment.id = object.id;
            comment.text = object.text;
            comment.author = object.by;
            comment.time = object.time;
            comment.level = object.level;
            comment.place = place;
            comment.storyId = object.storyId;

        return comment;
    }
}
