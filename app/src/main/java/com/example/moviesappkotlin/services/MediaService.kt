package com.example.moviesappkotlin.services

import com.example.moviesappkotlin.responses.MediaResponseList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaService {
    @GET("search/multi")
    fun multiSearch(
        @Query("api_key") apiKey: String, @Query("query") query: String) : Call<MediaResponseList>
}