package com.francescoalessi.hacky.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.francescoalessi.hacky.HackyApplication
import com.francescoalessi.hacky.databinding.FragmentFrontpageBinding
import com.francescoalessi.hacky.ui.adapter.FrontpageAdapter
import kotlinx.android.synthetic.main.fragment_frontpage.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class FrontpageFragment : Fragment()
{
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: FrontpageViewModel
    private lateinit var mAdapter: FrontpageAdapter

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
        val binding = FragmentFrontpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        val activity: FragmentActivity = activity as FragmentActivity
        viewModel =
            ViewModelProvider(activity, viewModelFactory).get(FrontpageViewModel::class.java)

        /*
         *  Set up the recyclerview
         */
        rv_stories.layoutManager = LinearLayoutManager(activity)
        mAdapter = FrontpageAdapter()
        rv_stories.adapter = mAdapter

        /*
         *  Set adapter's frontpage posts once they are available
         */
        fetchFrontpagePosts()

        /*
         *  Listen to changes in the Load states
         */
        mAdapter.addLoadStateListener { loadStates ->
            // Show loading widget when data is being loaded
            swipe_refresh_layout.isRefreshing = loadStates.refresh is LoadState.Loading

            val isError = loadStates.mediator?.refresh is LoadState.Error
                    && loadStates.source.refresh is LoadState.Error
            // Show error message if there is a connection error.
            tv_connection_error.visibility =
                when (isError)
                {
                    true -> View.VISIBLE
                    false -> View.INVISIBLE
                }
            rv_stories.visibility = // Hide the recyclerview when we show an error.
                when (isError)
                {
                    true -> View.INVISIBLE
                    false -> View.VISIBLE
                }
        }

        /*
         *  Trigger a refresh when the users swipes from the top
         */
        swipe_refresh_layout.setOnRefreshListener {
            mAdapter.refresh()
        }
    }

    /*
     *  Fetches Frontpage posts and submits them to RecyclerView Adapter.
     */
    @ExperimentalCoroutinesApi
    fun fetchFrontpagePosts()
    {
        // Fetch and submit
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }
}