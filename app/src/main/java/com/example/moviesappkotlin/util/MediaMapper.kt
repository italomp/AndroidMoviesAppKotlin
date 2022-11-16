package com.example.moviesappkotlin.util

import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.responses.MediaDetailsResponse
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList

class MediaMapper {
    companion object{
        fun fromMediaResponseToMedia(mediaResponseList: List<MediaResponse>?): List<Media>{
            val mediaList : MutableList<Media> = mutableListOf()
            if (mediaResponseList == null) return listOf()
            for(mediaResponse in mediaResponseList){
                val media = mediaResponse.getEntity()
                mediaList.add(media)
            }
            return  mediaList
        }

        fun fromMediaDetailsToMovie(mediaDetailsResponse: MediaDetailsResponse): Movie{
            return Movie(
                mediaDetailsResponse.id,
                mediaDetailsResponse.title,
                mediaDetailsResponse.posterPath,
                Constants.MOVIE_TYPE,
                mediaDetailsResponse.revenue
            )
        }
    }
}