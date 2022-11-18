package com.example.moviesappkotlin.services

import com.example.moviesappkotlin.responses.CrewResponse
import com.example.moviesappkotlin.responses.MediaDetailsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowService {
    @GET("tv/{tv_id}")
    fun getShowDetails(@Path("tv_id") id: Long, @Query("api_key") apiKey: String)
    : Call<MediaDetailsResponse>

    @GET("tv/{tv_id}/credits")
    fun getCreditsByShow(@Path("tv_id") movieId: Long, @Query("api_key") apiKey: String)
    : Call<CrewResponse>


}