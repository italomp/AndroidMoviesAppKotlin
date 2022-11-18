package com.example.moviesappkotlin.responses

import com.squareup.moshi.Json

class EmployeeResponse(
    @Json(name = "id")
    var id: Long,
    @Json(name = "name")
    var name: String,
    @Json(name = "department")
    var department: String,
    @Json(name = "profile_path")
    var profilePath: String?)