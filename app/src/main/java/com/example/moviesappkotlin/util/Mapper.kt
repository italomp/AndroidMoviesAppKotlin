package com.example.moviesappkotlin.util

import com.example.moviesappkotlin.models.Crew
import com.example.moviesappkotlin.models.Employee
import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.responses.*

class Mapper {
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

        fun fromCrewResponseToCrew(crewResponse: CrewResponse): Crew {
            val employeeList = mapperEmployeeResponseListToEmployeeList(crewResponse.crew)
            return Crew(employeeList)
        }

        fun mapperEmployeeResponseListToEmployeeList(empRespList: List<EmployeeResponse>)
        : List<Employee>{
            val result = mutableListOf<Employee>()
            for(empResp in empRespList){
                val newEmployee = Employee(
                    empResp.id, empResp.name, empResp.department, empResp.profilePath)
                result.add(newEmployee)
            }
            return result
        }
    }
}