package com.example.moviesappkotlin.models

class Show(
    override val id: Long,
    val name: String?,
    val posterPath: String?,
    override val mediaType: String?) : Media(id, mediaType)
