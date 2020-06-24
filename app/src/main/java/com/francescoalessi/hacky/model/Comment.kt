package com.francescoalessi.hacky.model

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.text.util.Linkify
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.marginLeft
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlin.math.roundToInt

/*
 *  Data class for HN comments
 */

@Entity(tableName = "comments")
@JsonClass(generateAdapter = true)
data class Comment(
    @PrimaryKey
    var id:Int,
    var parentId:Int?,
    var level:Int,
    var user:String?,
    var time:Int,
    var content:String,
    @Ignore
    var comments:List<Comment>?,
    var rank:Int?
)
{
    constructor() : this(-1, null, -1, "", -1, "", null, -1)
}
