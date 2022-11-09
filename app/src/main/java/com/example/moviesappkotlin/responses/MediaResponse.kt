package com.example.moviesappkotlin.responses

import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.models.Person
import com.example.moviesappkotlin.models.Show
import com.example.moviesappkotlin.util.Constants
import com.squareup.moshi.Json

data class MediaResponse(
    @Json(name = "id")
    val id : Long,
    @Json(name = "original_title")
    val title : String?,
    @Json(name = "original_name")
    val name: String?,
    @Json(name = "poster_path")
    val postPath : String?,
    @Json(name = "know_for")
    val moviesAndShows : MutableList<MediaResponse>?,
    @Json(name = "media_type")
    val mediaType : String?,
    @Json(name = "revenue")
    val revenue : Long?){

        fun getEntity(): Media {
            return if(Constants.MOVIE_TYPE == mediaType || title != null)
                Movie(id, title, postPath, Constants.MOVIE_TYPE, revenue)
            else if(Constants.PERSON_TYPE == mediaType || moviesAndShows != null)
                Person(id, name, mapperMediaResponseListToMediaList(), Constants.PERSON_TYPE)
            else if(Constants.SHOW_TYPE == mediaType || name != null)
                Show(id, name, postPath, Constants.SHOW_TYPE)
            else
                throw Exception("This media is an unknown media type")
        }

        private fun mapperMediaResponseListToMediaList() : List<Media>?{
            var mediaList : MutableList<Media> = mutableListOf()

            if(moviesAndShows == null) return null
            if(moviesAndShows!!.isEmpty()) return null

            for(mediaResponse in moviesAndShows!!){
                var media: Media
                var currentMediaType: String? = mediaResponse.mediaType

                media = when(currentMediaType){
                    Constants.MOVIE_TYPE -> Movie(mediaResponse.id, mediaResponse.title,
                        mediaResponse.postPath, mediaResponse.mediaType)
                    Constants.SHOW_TYPE -> Show(mediaResponse.id, mediaResponse.name,
                        mediaResponse.postPath, mediaResponse.mediaType)
                    else -> throw Exception("The person have a unknown media type")
                }
                mediaList.add(media)
            }
            return mediaList
        }


    }
