package com.francescoalessi.hacky.data.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.francescoalessi.hacky.api.HackerNewsService
import com.francescoalessi.hacky.data.HackyDatabase
import com.francescoalessi.hacky.model.Comment
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CommentsRemoteMediatorTest
{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var hackyDatabase: HackyDatabase

    @Before
    fun initDb()
    {
        hackyDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            HackyDatabase::class.java
        ).build()
    }

    @After
    fun closeDb()
    {
        hackyDatabase.close()
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun saveCommentsToDatabase_shouldSaveCommentsToDatabase()
    {
        runBlockingTest {
            val comments = FakeCommentFactory.makeCommentList(100)
            val mockService: HackerNewsService = mock()
            val spyDatabase = spy(hackyDatabase)
            whenever(spyDatabase.postDao()).thenReturn(spy(hackyDatabase.postDao()))

            val mediator = CommentsRemoteMediator(spyDatabase, mockService)

            mediator.saveCommentsToDb(comments, LoadType.APPEND)

            verify(spyDatabase.postDao()).insertComments(any())
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun saveCommentsToDatabase_shouldSaveCommentsToDatabase_withSameCommentsProvidedByApi()
    {
        runBlockingTest {
            val comments = FakeCommentFactory.makeCommentList(100)
            val mockService: HackerNewsService = mock()
            val spyDatabase = spy(hackyDatabase)
            whenever(spyDatabase.postDao()).thenReturn(spy(hackyDatabase.postDao()))

            val mediator = CommentsRemoteMediator(spyDatabase, mockService)

            mediator.saveCommentsToDb(comments, LoadType.APPEND)

            argumentCaptor<List<Comment>> {
                verify(spyDatabase.postDao()).insertComments(capture())

                assertEquals(firstValue.size, comments.size)
                assertEquals(firstValue, comments)
            }

        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun saveCommentsToDatabase_whenRefreshLoadType_shouldDeleteThreadComments()
    {
        runBlockingTest {
            val comments = FakeCommentFactory.makeCommentList(100)
            val mockService: HackerNewsService = mock()
            val spyDatabase = spy(hackyDatabase)
            whenever(spyDatabase.postDao()).thenReturn(spy(hackyDatabase.postDao()))

            val mediator = CommentsRemoteMediator(spyDatabase, mockService)

            mediator.saveCommentsToDb(comments, LoadType.REFRESH)

            verify(spyDatabase.postDao()).deleteCommentsForThread(mediator.threadId)
        }
    }

    object FakeCommentFactory // TODO: Extract to shared test directory
    {
        val faker = Faker()

        fun makeComment(): Comment
        {
            return Comment(
                faker.number().numberBetween(0, Int.MAX_VALUE),
                faker.number().numberBetween(0, Int.MAX_VALUE),
                faker.number().randomDigit(),
                faker.name().username(),
                faker.number().numberBetween(1, 1000),
                faker.lorem().paragraph(),
                listOf(),
                faker.number().randomDigit())
        }

        fun makeCommentList(size: Int): List<Comment>
        {
            return MutableList<Comment>(size) {
                makeComment()
            }
        }
    }
}