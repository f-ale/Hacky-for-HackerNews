package com.francescoalessi.hacky.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.francescoalessi.hacky.MainCoroutineRule
import com.francescoalessi.hacky.data.HackyRepository
import com.francescoalessi.hacky.model.Post
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class FrontpageViewModelTest
{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Test
    fun getPosts_shouldRetrievePostsFromRepository()
    {
        runBlockingTest {
            val mockRepository: HackyRepository = mock()

            val postsList = listOf(FakePostFactory.makePost(),
                FakePostFactory.makePost(),
                FakePostFactory.makePost())
            whenever(mockRepository.getPosts()).thenReturn(flowOf(PagingData.from(postsList)))
            val frontpageViewModel = FrontpageViewModel(mockRepository)

            frontpageViewModel.posts

            verify(mockRepository).getPosts()
        }
    }
}

object FakePostFactory // TODO: Extract to shared test directory
{
    private val faker = Faker()

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