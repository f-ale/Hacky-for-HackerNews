package com.francescoalessi.hackernews.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item
{
    public int id;
    private boolean deleted;
    private String type;
    public String by;
    public int time;
    public String text;
    private boolean dead;
    private int parent;
    private int poll;
    private int[] kids;
    private String url;
    private int score;
    private String title;
    private JSONArray parts;
    private int descendants;
    public long storyId;
    public JSONArray children;
    public int level;
    private String timeAgo;

    public static Item parseNodeApiItem(JSONObject object)
    {
        Item item = new Item();
        try
        {
            item.id = object.getInt("id");
            item.title = object.optString("title", "No title");
            item.text = object.optString("content", "");
            item.deleted = object.optBoolean("deleted", false);
            item.type = object.optString("type");
            item.by = object.optString("user");
            item.time = object.optInt("time");
            item.dead = object.optBoolean("dead", false);
            item.parent = object.optInt("parentId");
            item.poll = object.optInt("poll");
            item.children = object.optJSONArray("comments");
            item.url = object.optString("domain");
            item.score = object.optInt("points", -1);
            item.parts = object.optJSONArray("parts");
            item.timeAgo = object.optString("time_ago");
            item.level = object.optInt("level");
            item.descendants = item.children.length();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        return item;
    }

    public static Item parseAlgoliaItem(JSONObject object)
    {
        Item item = new Item();
        try
        {
            item.id = object.getInt("id");
            item.title = object.optString("title", "No title");
            item.text = object.optString("text", "");
            item.deleted = object.optBoolean("deleted", false);
            item.type = object.optString("type");
            item.by = object.optString("author");
            item.time = object.optInt("created_at_i");
            item.dead = object.optBoolean("dead", false);
            item.parent = object.optInt("parentId");
            item.poll = object.optInt("poll");
            item.children = object.optJSONArray("children");
            item.url = object.optString("url");
            item.score = object.optInt("points", -1);
            item.parts = object.optJSONArray("parts");
            item.descendants = item.children.length();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        return item;
    }

    public static Item parseItem(JSONObject object)
    {
        Item item = new Item();
        try
        {
            item.id = object.getInt("id");
            item.title = object.optString("title", "No title");
            item.text = object.optString("text", "");
            item.deleted = object.optBoolean("deleted", false);
            item.type = object.optString("type");
            item.by = object.optString("by");
            item.time = object.optInt("time");
            item.dead = object.optBoolean("dead", false);
            item.parent = object.optInt("parent");
            item.poll = object.optInt("poll");
            item.kids = parseIntJsonArray(object.optJSONArray("kids"));
            item.url = object.optString("url");
            item.score = object.optInt("score", -1);
            item.parts = object.optJSONArray("parts");
            item.descendants = object.optInt("descendants");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        return item;
    }

    private static int[] parseIntJsonArray(JSONArray array)
    {
        int[] newArray = null;
        if(array == null)
            return null;
        try
        {
            newArray = new int[array.length()];
            for(int i = 0; i < array.length(); i++)
            {
                newArray[i] = array.getInt(i);
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return newArray;
    }
}


