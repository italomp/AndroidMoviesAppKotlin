package com.example.moviesappkotlin.responses

import com.squareup.moshi.Json

data class MediaResponseList(
    @Json(name = "results")
    val responseList: List<MediaResponse>)