package com.francescoalessi.hacky.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/*
 *  Data class for HN posts
 */

@Entity(tableName = "posts")
@JsonClass(generateAdapter = true)
data class Post
(
    @PrimaryKey
    val id:Int,
    val title:String,
    val points:Int?,
    val user:String?,
    val time:Int,
    val comments_count:Int,
    val type:String,
    val url:String?,
    val domain:String?,
    var rank:Int?
)
