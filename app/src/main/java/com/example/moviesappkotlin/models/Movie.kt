package com.example.moviesappkotlin.models

class Movie : Media {
    override val id: Long
    override val title: String?
    private val posterPath: String?
    private val revenue: Long?

    constructor(id: Long, title: String?, posterPath: String?, mediaType: String, revenue: Long?) :
            super(id, mediaType, title){
        this.id = id
        this.title = title
        this.posterPath = posterPath
        this.revenue = revenue
    }

    constructor(id: Long, title: String?, posterPath: String?, mediaType: String) :
            super(id, mediaType, title){
        this.id = id
        this.title = title
        this.posterPath = posterPath
        this.revenue = 0
    }
}