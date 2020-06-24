package com.francescoalessi.hacky.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.francescoalessi.hacky.model.Comment
import com.francescoalessi.hacky.model.Post

@Database(entities = [Post::class, Comment::class], version = 13)
abstract class HackyDatabase : RoomDatabase()
{
    abstract fun postDao(): PostDao
}