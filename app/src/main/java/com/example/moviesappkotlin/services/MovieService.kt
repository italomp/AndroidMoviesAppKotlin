package com.example.moviesappkotlin.services

import com.example.moviesappkotlin.responses.CrewResponse
import com.example.moviesappkotlin.responses.MediaDetailsResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("movie/popular")
    fun getMovies(@Query("api_key") apiKey: String) : Call<MediaResponseList>

    @GET("discover/movie")
    fun getMoviesByYear(@Query("api_key") apiKey: String,
                        @Query("primary_release_year") year: Int,
                        @Query("sort_by") sortBy: String): Call<MediaResponseList>

    @GET("movie/{id}")
    fun getMovieDetails(@Path("id") id: Long,
                        @Query("api_key") apiKey: String?): Call<MediaDetailsResponse>
    /*@GET("movie/{movie_id}/credits")
    Call<CrewResponse> getCreditsByMovie(@Path("movie_id") long movieId, @Query("api_key") String apiKey);*/

    @GET("movie/{movie_id}/credits")
    fun getCreditsByMovie(@Path("movie_id") movieId: Long,
                          @Query("api_key") apiKey: String): Call<CrewResponse>

}