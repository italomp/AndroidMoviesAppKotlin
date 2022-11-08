package com.example.moviesappkotlin.models

import com.example.moviesappkotlin.util.Constants
import java.util.*

open class Media {
    protected open val id: Long?
    protected open val mediaType: String?
    protected open val title : String?
    protected open val moviesAndShows : List<Media>?

    constructor(id: Long?, mediaType: String?) {
        this.id = id
        this.mediaType = mediaType
        moviesAndShows = null
        title = null
    }

    constructor(id: Long?, mediaType: String?, title: String?) {
        this.id = id
        this.mediaType = mediaType
        this.title = title
        moviesAndShows = null
    }

    constructor(id: Long?, mediaType: String?, moviesAndShows: List<Media>?) {
        this.id = id
        this.mediaType = mediaType
        this.moviesAndShows = moviesAndShows
        title = null
    }

    fun getSubType(): String{
        return if(title != null || Constants.MOVIE_TYPE == mediaType)
            Constants.MOVIE_TYPE
        else if (Constants.PERSON_TYPE == mediaType || moviesAndShows != null)
            Constants.PERSON_TYPE
        else
            Constants.SHOW_TYPE
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val media = o as Media
        return id == media.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}