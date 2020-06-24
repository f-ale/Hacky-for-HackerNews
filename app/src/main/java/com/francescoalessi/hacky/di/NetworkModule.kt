package com.francescoalessi.hacky.di

import com.francescoalessi.hacky.api.HackerNewsService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule
{
    private val baseUrl = "https://api.hackerwebapp.com"

    /*
     *   Provides Retrofit HackerNews Service for injection
     */
    @Singleton
    @Provides
    fun provideHackerNewsService(): HackerNewsService
    {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(HackerNewsService::class.java)
    }
}