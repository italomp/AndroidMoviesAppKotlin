package com.example.moviesappkotlin.services

import com.example.moviesappkotlin.responses.MediaResponseList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("movie/popular")
    fun getMovies(@Query("api_key") apiKey: String) : Call<MediaResponseList>

    @GET("discover/movie")
    fun getMoviesByYear(@Query("api_key") apiKey: String,
                        @Query("primary_release_year") year: Int,
                        @Query("sort_by") sortBy: String): Call<MediaResponseList>
}