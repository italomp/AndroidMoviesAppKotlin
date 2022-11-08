package com.example.moviesappkotlin.util

import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList

class MediaMapper {
    companion object{
        fun fromMediaResponseToMedia(mediaResponseList: List<MediaResponse>?): List<Media>{
            var mediaList : MutableList<Media> = mutableListOf()
            if (mediaResponseList == null) return listOf()
            for(mediaResponse in mediaResponseList){
                var media = mediaResponse.getEntity()
                mediaList.add(media)
            }
            return  mediaList
        }
    }
}