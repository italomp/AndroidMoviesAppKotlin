package com.example.moviesappkotlin.responses

import com.squareup.moshi.Json

class CrewResponse( @Json(name = "crew")
                    var crew: List<EmployeeResponse>)