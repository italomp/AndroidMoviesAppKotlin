package com.example.moviesappkotlin.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.moviesappkotlin.models.Media

class Util {
    companion object{
        fun isItMovie(media: Media): Boolean{
            return Constants.MOVIE_TYPE == media.mediaType
        }

        fun isItShow(media: Media): Boolean{
            return Constants.SHOW_TYPE == media.mediaType
        }

        fun isItPerson(media: Media): Boolean{
            return Constants.PERSON_TYPE == media.mediaType
        }

        fun showMessage(context: Context, msg: String){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        fun showProgressBarAndHiddenView(progressBar: View, views: Array<View>){
            progressBar.visibility = View.VISIBLE
            for(view in views){
                view.visibility = View.INVISIBLE
            }
        }

        fun hiddenProgressBarAndShowView(progressBar: View, views: Array<View>){
            progressBar.visibility = View.INVISIBLE
            for(view in views){
                view.visibility = View.VISIBLE
            }
        }

        fun hiddenProgressBar(progressBar: View){
            progressBar.visibility = View.INVISIBLE
        }
    }
}