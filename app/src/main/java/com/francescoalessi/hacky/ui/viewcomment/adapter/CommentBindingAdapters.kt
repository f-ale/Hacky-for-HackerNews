package com.francescoalessi.hacky.ui.viewcomment.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.text.util.Linkify
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.francescoalessi.hacky.model.Comment
import kotlin.math.roundToInt

@BindingAdapter("commentContent")
fun bindCommentContent(textView: TextView, rawContent: String)
{
    textView.text = rawContent
    Linkify.addLinks(textView, Linkify.WEB_URLS)
    textView.text =
        (HtmlCompat.fromHtml(textView.text.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT))
            .trimEnd()
}

@BindingAdapter("commentColor")
fun bindCommentColor(imageView: ImageView, comment: Comment)
{
    if (comment.level > 0)
        imageView.setImageDrawable(
            ColorDrawable(
                stringToColor(comment.user ?: "user")
            )
        )
    else
        imageView.setImageDrawable(null)
}

@BindingAdapter("timeAgo")
fun bindTimeAgo(textView: TextView, time: Int)
{
    textView.text = DateUtils.getRelativeTimeSpanString(time.toLong() * 1000)
}

@BindingAdapter("level")
fun bindLevel(layout: ConstraintLayout, level: Int)
{
    val density: Float = layout.context.resources.displayMetrics.density
    val marginParams = (layout.layoutParams as ViewGroup.MarginLayoutParams)

    marginParams.setMargins(
        (8 * density * level).roundToInt(),
        marginParams.topMargin,
        marginParams.rightMargin,
        (1 * density).roundToInt()
    )
}

/*
 *  Hashing function that generates a consistent color value given a string
 */
private fun stringToColor(string: String): Int
{
    val hash = string.hashCode()
    val r = hash and 0xFF0000 shr 16
    val g = hash and 0x00FF00 shr 8
    val b = hash and 0x0000FF
    val hsv = FloatArray(3)
    Color.RGBToHSV(r, g, b, hsv)
    //hsv[0] = hash%359;
    hsv[2] = 0.8.toFloat()
    hsv[1] = 0.9.toFloat()
    return Color.HSVToColor(hsv)
}