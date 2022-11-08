package com.example.moviesappkotlin.models

import java.util.*

open class Person(
    override val id: Long,
    private val name: String?,
    override val moviesAndShows: List<Media>?,
    override val mediaType: String) : Media(id, mediaType, moviesAndShows)