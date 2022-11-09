package com.example.moviesappkotlin.services

import com.example.moviesappkotlin.util.Constants.Companion.API_BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiService {
    companion object{
        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private val retrofit: Retrofit by lazy{
            Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        }
        val movieService: MovieService by lazy {
            retrofit.create(MovieService::class.java)
        }
    }
}