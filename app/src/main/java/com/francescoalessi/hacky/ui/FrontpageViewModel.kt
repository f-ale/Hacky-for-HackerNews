package com.francescoalessi.hacky.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.francescoalessi.hacky.data.HackyRepository
import com.francescoalessi.hacky.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FrontpageViewModel @Inject constructor(private val repository: HackyRepository) : ViewModel()
{
    val posts: Flow<PagingData<Post>> = repository.getPosts().cachedIn(viewModelScope)
}