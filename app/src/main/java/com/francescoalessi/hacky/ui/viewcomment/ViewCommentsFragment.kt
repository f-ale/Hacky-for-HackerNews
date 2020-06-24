package com.francescoalessi.hacky.ui.viewcomment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.francescoalessi.hacky.HackyApplication
import com.francescoalessi.hacky.R
import com.francescoalessi.hacky.databinding.FragmentViewCommentsBinding
import com.francescoalessi.hacky.model.Post
import com.francescoalessi.hacky.ui.viewcomment.adapter.CommentAdapter
import kotlinx.android.synthetic.main.fragment_view_comments.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ViewCommentsFragment : Fragment()
{
    private val args: ViewCommentsFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CommentViewModel
    private lateinit var mAdapter: CommentAdapter
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context)
    {
        // Enable DI on the fragment
        (activity?.applicationContext as HackyApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment using Data Binding
        val binding = FragmentViewCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.menu_read_comments, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_share ->
            {
                // Share the post in text form
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    " ${post.title} \n https://news.ycombinator.com/item?id=${post.id}"
                )
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, post.title)
                sendIntent.type = "text/plain"

                val shareIntent = Intent.createChooser(sendIntent, "Share: " + post.title)

                if (sendIntent.resolveActivity(activity?.packageManager!!) != null) startActivity(
                    shareIntent
                )
            }

            R.id.action_view_url ->
            {
                // View the post URL
                val viewIntent = Intent()
                viewIntent.action = Intent.ACTION_VIEW
                viewIntent.data = Uri.parse(post.url)

                if (viewIntent.resolveActivity(activity?.packageManager!!) != null) startActivity(
                    viewIntent
                )
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        // Retrieve the ViewModel
        val activity: FragmentActivity = activity as FragmentActivity
        viewModel = ViewModelProvider(activity, viewModelFactory).get(CommentViewModel::class.java)

        // Retrieve the post Id
        val threadId = args.postId

        /*
         *  Retrieve the post
         */
        viewModel.getPost(threadId).observe(viewLifecycleOwner, Observer {
            post = it
        })

        /*
         *  Set up the recyclerview
         */
        rv_comments.layoutManager = LinearLayoutManager(activity)
        mAdapter =
            CommentAdapter()
        rv_comments.adapter = mAdapter

        /*
         *  Set adapter's frontpage posts once they are available
         */

        fetchCommentsForThread(threadId)

        /*
         *  Show error message if there is a connection error.
         */

        mAdapter.addLoadStateListener { loadStates ->
            // Show loading widget when data is being loaded
            swipe_refresh_layout.isRefreshing = loadStates.refresh is LoadState.Loading

            // Show error message if there is a connection error.
            tv_connection_error.visibility =
                when (loadStates.refresh)
                {
                    is LoadState.Error -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            rv_comments.visibility = // Hide the recyclerview when we show an error.
                when (loadStates.refresh)
                {
                    is LoadState.Error -> View.INVISIBLE
                    else -> View.VISIBLE
                }

        }

        swipe_refresh_layout.setOnRefreshListener {
            mAdapter.refresh() // Refresh data when the user requests it
        }
    }

    /*
     *  Fetches Frontpage posts and submits them to RecyclerView Adapter.
     */
    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    fun fetchCommentsForThread(threadId: Int)
    {
        // Fetch and submit
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCommentsForThread(threadId).collectLatest {
                mAdapter.submitData(it)
            }
        }
    }
}