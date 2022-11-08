package com.example.moviesappkotlin.services

import com.example.moviesappkotlin.util.Constants.Companion.API_BASE_URL
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiService {
    companion object{
        private var mediaService : MediaService? = null
        private var movieService : MovieService? = null

        fun getMediaService() : MediaService?{
            if(mediaService == null){
                val retrofit : Retrofit = Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

                mediaService = retrofit.create(MediaService::class.java)
            }
            return mediaService
        }

        fun getMovieService() : MovieService?{
            if(movieService == null){
//                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

                movieService = retrofit.create(MovieService::class.java)
            }
            return movieService
        }
    }
}