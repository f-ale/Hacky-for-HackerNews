package com.francescoalessi.hacky.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.francescoalessi.hacky.data.network.CommentsRemoteMediator
import com.francescoalessi.hacky.data.network.PostsRemoteMediator
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class HackyRepositoryTest
{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val faker = Faker()

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun getPosts_shouldGetPostsFromDatabase()
    {
        runBlockingTest {
            // Given
            val mockDatabase: HackyDatabase = mock()
            val mockPostMediator: PostsRemoteMediator = mock()
            val mockCommentsMediator: CommentsRemoteMediator = mock()
            val mockDao : PostDao = mock()
            whenever(mockDatabase.postDao()).thenReturn(mockDao)
            whenever(mockDao.getPosts()).thenReturn(mock())
            val repository = HackyRepository(mockDatabase, mockPostMediator, mockCommentsMediator)

            // When the repository gets the posts
            repository.getPosts().first()

            // Verify that it called the database
            verify(mockDao).getPosts()
        }
    }

    @ExperimentalPagingApi
    @Test
    fun getPost_shouldGetPostFromDatabase()
    {
        // Given
        val mockDatabase: HackyDatabase = mock()
        val mockPostMediator: PostsRemoteMediator = mock()
        val mockCommentsMediator: CommentsRemoteMediator = mock()
        val mockDao : PostDao = mock()
        whenever(mockDatabase.postDao()).thenReturn(mockDao)
        val repository = HackyRepository(mockDatabase, mockPostMediator, mockCommentsMediator)

        val id = faker.number().randomDigit()

        // When
        repository.getPost(id)

        // Verify it fetched from database
        verify(mockDao).getPostForId(any())
    }

    @ExperimentalPagingApi
    @Test
    fun getPost_shouldGetPostFromDatabase_withSameId()
    {
        // Given
        val mockDatabase: HackyDatabase = mock()
        val mockPostMediator: PostsRemoteMediator = mock()
        val mockCommentsMediator: CommentsRemoteMediator = mock()
        val mockDao : PostDao = mock()
        whenever(mockDatabase.postDao()).thenReturn(mockDao)
        val repository = HackyRepository(mockDatabase, mockPostMediator, mockCommentsMediator)

        val id = faker.number().randomDigit()

        // When
        repository.getPost(id)

        // Verify it fetched from database
        verify(mockDao).getPostForId(id)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun getCommentsForThread_shouldGetCommentsFromDatabase()
    {
        runBlockingTest {
            // Given
            val mockDatabase: HackyDatabase = mock()
            val mockPostMediator: PostsRemoteMediator = mock()
            val mockCommentsMediator: CommentsRemoteMediator = mock()
            val mockDao : PostDao = mock()
            whenever(mockDatabase.postDao()).thenReturn(mockDao)
            whenever(mockDao.getCommentsForThread(any())).thenReturn(mock())
            val repository = HackyRepository(mockDatabase, mockPostMediator, mockCommentsMediator)

            val id = faker.number().randomDigit()

            // When the repository gets the posts
            repository.getCommentsForThread(id).first()

            // Verify that it called the database
            verify(mockDao).getCommentsForThread(any())
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun getCommentsForThread_shouldGetCommentsFromDatabase_withSameId()
    {
        runBlockingTest {
            // Given
            val mockDatabase: HackyDatabase = mock()
            val mockPostMediator: PostsRemoteMediator = mock()
            val mockCommentsMediator: CommentsRemoteMediator = mock()
            val mockDao : PostDao = mock()
            whenever(mockDatabase.postDao()).thenReturn(mockDao)
            whenever(mockDao.getCommentsForThread(any())).thenReturn(mock())
            val repository = HackyRepository(mockDatabase, mockPostMediator, mockCommentsMediator)

            val id = faker.number().randomDigit()

            // When the repository gets the posts
            repository.getCommentsForThread(id).first()

            // Verify that it called the database
            verify(mockDao).getCommentsForThread(id)
        }
    }
}