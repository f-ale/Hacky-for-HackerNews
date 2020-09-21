package com.francescoalessi.hacky.data.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.francescoalessi.hacky.api.HackerNewsService
import com.francescoalessi.hacky.data.HackyDatabase
import com.francescoalessi.hacky.model.Post
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostsRemoteMediatorTest
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
    fun savePostsToDb_shouldSavePostsToDatabase()
    {
        runBlockingTest{
            val posts = FakePostFactory.makePostList(100)
            val mockService: HackerNewsService = mock()
            val spyDatabase = spy(hackyDatabase)
            whenever(spyDatabase.postDao()).thenReturn(spy(hackyDatabase.postDao()))

            val mediator = PostsRemoteMediator(spyDatabase, mockService)

            mediator.savePostsToDb(posts, LoadType.APPEND)

            verify(spyDatabase.postDao()).insertPosts(any())
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun savePostsToDb_shouldSavePostsToDatabase_withSamePostsProvidedByApi()
    {
        runBlockingTest{
            val posts = FakePostFactory.makePostList(100)
            val mockService: HackerNewsService = mock()
            val spyDatabase = spy(hackyDatabase)
            whenever(spyDatabase.postDao()).thenReturn(spy(hackyDatabase.postDao()))

            val mediator = PostsRemoteMediator(spyDatabase, mockService)

            mediator.savePostsToDb(posts, LoadType.APPEND)

            argumentCaptor<List<Post>>{
                verify(spyDatabase.postDao()).insertPosts(capture())

                assertEquals(firstValue.size, posts.size)
                assertEquals(firstValue, posts)
            }

        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun savePostsToDb_whenRefreshLoadType_shouldDeleteOldPosts()
    {
        runBlockingTest{
            val posts = FakePostFactory.makePostList(100)
            val mockService: HackerNewsService = mock()
            val spyDatabase = spy(hackyDatabase)
            whenever(spyDatabase.postDao()).thenReturn(spy(hackyDatabase.postDao()))

            val mediator = PostsRemoteMediator(spyDatabase, mockService)

            mediator.savePostsToDb(posts, LoadType.REFRESH)

            verify(spyDatabase.postDao()).deleteAllPosts()
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun getPostsAndAssignRank_shouldGetPostsFromApi()
    {
        runBlockingTest {
            val mockService: HackerNewsService = mock()
            val posts = FakePostFactory.makePostList(30)
            whenever(mockService.getPosts(any())).thenReturn(posts)

            val mediator = PostsRemoteMediator(hackyDatabase, mockService)

            mediator.getPostsAndAssignRank()

            verify(mockService).getPosts(any())
        }
    }
}

object FakePostFactory // TODO: Extract to shared test directory
{
    val faker = Faker()

    fun makePost() : Post
    {
        return Post(faker.number().randomDigit(),
            faker.lordOfTheRings().character(),
            faker.number().numberBetween(1,100),
            faker.name().username(),
            faker.number().randomDigit(),
            faker.number().numberBetween(0, 1000),
            faker.name().title(),
            faker.internet().url(),
            faker.internet().domainName(),
            faker.number().randomDigit())
    }

    fun makePostList(size:Int) : List<Post>
    {
        return  MutableList<Post>(size) {
            makePost()
        }
    }
}