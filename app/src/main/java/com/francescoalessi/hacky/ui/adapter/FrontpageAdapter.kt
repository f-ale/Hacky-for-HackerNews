package com.francescoalessi.hacky.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.francescoalessi.hacky.databinding.ThreadItemBinding
import com.francescoalessi.hacky.model.Post
import com.francescoalessi.hacky.ui.FrontpageFragmentDirections

class FrontpageAdapter : PagingDataAdapter<Post, FrontpageAdapter.ViewHolder>(DIFF_CALLBACK)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = ThreadItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = getItem(position)
        holder.binding.post = item
    }

    fun openComments(viewHolder: ViewHolder)
    {
        if (viewHolder.binding.post?.comments_count ?: 0 > 0)
        {
            viewHolder.itemView.findNavController().navigate(
                FrontpageFragmentDirections.actionFrontpageToViewCommentsFragment(
                    viewHolder.binding.post?.id ?: -1,
                    viewHolder.binding.post?.title ?: ""
                )
            )
        }
    }

    inner class ViewHolder(val binding: ThreadItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        init
        {
            // Handle onClickListener
            binding.setClickListener {
                val url: String? = binding.post?.url
                if (!url.isNullOrBlank())
                {
                    val viewURLIntent = Intent(Intent.ACTION_VIEW)
                    viewURLIntent.data = Uri.parse(url)
                    if (viewURLIntent.resolveActivity(it.context?.packageManager!!) != null)
                    {
                        it.context.startActivity(viewURLIntent)
                    }
                    else
                        openComments(this)

                }
                else
                {
                    openComments(this)
                }
            }

            binding.setCommentsClickListener {
                openComments(this)
            }
        }
    }
}

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>()
{
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean
    {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean
    {
        return oldItem == newItem
    }

}