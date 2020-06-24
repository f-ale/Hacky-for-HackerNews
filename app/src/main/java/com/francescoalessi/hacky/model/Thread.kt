package com.francescoalessi.hacky.model

import com.squareup.moshi.JsonClass

/*
 *  Data class for HN threads.
 *  Used for deserialization only.
 */

@JsonClass(generateAdapter = true)
data class Thread(
    val id:Int,
    val title:String,
    val points:Int?,
    val user:String?,
    val time:Int?,
    val type:String?,
    val url:String?,
    val domain:String?,
    val comments:List<Comment>,
    val comments_count:Int
)