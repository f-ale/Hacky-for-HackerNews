package com.francescoalessi.hacky.ui.viewcomment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.francescoalessi.hacky.databinding.CommentItemBinding
import com.francescoalessi.hacky.model.Comment

class CommentAdapter : PagingDataAdapter<Comment, CommentAdapter.ViewHolder>(
    DIFF_CALLBACK
)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CommentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        binding.comment = item
    }

    inner class ViewHolder(val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root)
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>()
{
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean
    {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean
    {
        return oldItem == newItem
    }

}