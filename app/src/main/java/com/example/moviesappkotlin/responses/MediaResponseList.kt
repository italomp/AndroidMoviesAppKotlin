package com.example.moviesappkotlin.responses

import com.squareup.moshi.Json

class MediaResponseList(
    @Json(name = "results")
    val responseList: List<MediaResponse>)