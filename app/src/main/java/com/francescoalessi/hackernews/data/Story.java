package com.francescoalessi.hackernews.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "stories")
public class Story
{
    @PrimaryKey
    public int id;

    @NonNull
    public int place;

    @NonNull
    public String title;
    @NonNull
    public String author;

    public String text;
    public String url;
    public int score;
    public long time;
    public int comments;

    @Ignore
    public static Story parse(JSONObject object, int place)
    {
        Story story = new Story();
        try
        {
            story.id = object.getInt("id");
            story.title = object.optString("title", "No title");
            story.text = object.optString("text", "");
            story.author = object.optString("by");
            story.time = object.optInt("time");
            story.url = object.optString("url");
            story.score = object.optInt("score", -1);
            story.comments = object.optInt("descendants");
            story.place = place;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        return story;
    }
}


