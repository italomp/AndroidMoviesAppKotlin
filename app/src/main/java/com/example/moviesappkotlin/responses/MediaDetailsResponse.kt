package com.example.moviesappkotlin.responses

import com.squareup.moshi.Json

class MediaDetailsResponse(
    @Json(name = "id")
    var id: Long,
    @Json(name = "original_title")
    var title: String?,
    @Json(name = "poster_path")
    var posterPath: String?,
    @Json(name = "revenue")
    var revenue: Long?,
    @Json(name = "overview")
    var overview: String?,
    @Json(name = "vote_average")
    var voteAverage: Float ){

    init {
        voteAverage *= 10
    }

}