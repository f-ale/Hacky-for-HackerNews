package com.francescoalessi.hacky.ui.viewcomment

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.francescoalessi.hacky.data.HackyRepository
import com.francescoalessi.hacky.model.Comment
import com.francescoalessi.hacky.model.Post
import com.francescoalessi.hacky.ui.FakePostFactory
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class CommentViewModelTest
{
    val faker = Faker()

    @ExperimentalPagingApi
    @Test
    fun getCommentsForThread_getsCommentsFromRepository()
    {
        val mockRepository: HackyRepository = mock()
        val commentViewModel = CommentViewModel(mockRepository)

        whenever(mockRepository.getCommentsForThread(any()))
            .thenReturn(flowOf(
                PagingData.from(listOf<Comment>())
            ))

        val threadId = faker.number().numberBetween(0, Int.MAX_VALUE)
        commentViewModel.getCommentsForThread(threadId)

        verify(mockRepository).getCommentsForThread(any())
    }

    @ExperimentalPagingApi
    @Test
    fun getCommentsForThread_getsCommentsFromRepository_withCorrectId()
    {
        val mockRepository: HackyRepository = mock()
        val commentViewModel = CommentViewModel(mockRepository)

        whenever(mockRepository.getCommentsForThread(any()))
            .thenReturn(flowOf(
                PagingData.from(listOf<Comment>())
            ))

        val threadId = faker.number().numberBetween(0, Int.MAX_VALUE)
        commentViewModel.getCommentsForThread(threadId)

        verify(mockRepository).getCommentsForThread(threadId)
    }


    @Test
    fun getPost_getsPostFromRepository()
    {
        val mockRepository: HackyRepository = mock()
        val commentViewModel = CommentViewModel(mockRepository)

        whenever(mockRepository.getPost(any()))
            .thenReturn(MutableLiveData<Post>(FakePostFactory.makePost()))

        val postId = faker.number().numberBetween(0, Int.MAX_VALUE)

        commentViewModel.getPost(postId)

        verify(mockRepository).getPost(any())
    }

    @Test
    fun getPost_getsPostFromRepository_withCorrectId()
    {
        val mockRepository: HackyRepository = mock()
        val commentViewModel = CommentViewModel(mockRepository)

        whenever(mockRepository.getPost(any()))
            .thenReturn(MutableLiveData<Post>(FakePostFactory.makePost()))

        val postId = faker.number().numberBetween(0, Int.MAX_VALUE)

        commentViewModel.getPost(postId)

        verify(mockRepository).getPost(postId)
    }
}