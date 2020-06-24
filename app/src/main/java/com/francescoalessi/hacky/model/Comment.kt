package com.francescoalessi.hacky.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/*
 *  Data class for HN comments
 */

@Entity(tableName = "comments")
@JsonClass(generateAdapter = true)
data class Comment(
    @PrimaryKey
    var id: Int,
    var parentId: Int?,
    var level: Int,
    var user: String?,
    var time: Int,
    var content: String,
    @Ignore
    var comments: List<Comment>?,
    var rank: Int?
)
{
    constructor() : this(-1, null, -1, "", -1, "", null, -1)
}
