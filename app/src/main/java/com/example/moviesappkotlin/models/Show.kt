package com.example.moviesappkotlin.models

class Show(
    override val id: Long,
    private val name: String?,
    private val posterPath: String?,
    override val mediaType: String?) : Media(id, mediaType)
