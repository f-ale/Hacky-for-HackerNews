package com.francescoalessi.hacky.api

import com.francescoalessi.hacky.model.Post
import com.francescoalessi.hacky.model.Thread
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HackerNewsService {
    /*
     *  Defines the query to retrieve search results from the HN API
     */
    @GET("/news")
    suspend fun getPosts(
        @Query("page") page: Int
    ) : List<Post>

    @GET("/item/{id}")
    suspend fun getThread(
        @Path("id") id: Int
    ) : Thread
}