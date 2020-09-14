package com.francescoalessi.hacky.data.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.francescoalessi.hacky.data.HackyDatabase
import com.francescoalessi.hacky.model.Post
import com.github.javafaker.Faker
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostsRemoteMediatorTest
{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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

    // TODO: Find a way to properly test PagingData
/*
    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @Test
    fun load_typeRefresh_getsPostsFromHackerNewsService()
    {
        runBlockingTest {
            val mockService: HackerNewsService = mock()
            val mediator = PostsRemoteMediator(hackyDatabase, mockService)

            val postList = listOf(FakePostFactory.makePost(),
                FakePostFactory.makePost(),
                FakePostFactory.makePost())

            whenever(mockService.getPosts(any())).thenReturn(postList)

            val result = mediator.load(
                LoadType.APPEND,
                PagingState(listOf(),
                    0,
                    PagingConfig(30),
                    0)
            )

            // Verify
            verify(mockService).getPosts(any())
        }
    }
   */
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
}