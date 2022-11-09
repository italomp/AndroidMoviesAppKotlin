package com.example.moviesappkotlin.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.models.Show
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants
import com.example.moviesappkotlin.util.Constants.Companion.API_KEY
import com.example.moviesappkotlin.util.MediaMapper
import com.example.moviesappkotlin.util.Util
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Stream
import java.util.zip.Inflater

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout : TabLayout
    private lateinit var gridLayout : GridLayout
    private lateinit var searchView : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViews()
        getMovies()
//        multiSearch()
    }

    private fun setViews(){
        tabLayout = findViewById(R.id.tab_layout)
        gridLayout = findViewById(R.id.grid_layout)
        setSearchView()
    }

    private fun setSearchView(){
        searchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Send request
                return false
            }
        })
    }

    private fun getMovies(){
        ApiService.movieService.getMovies(API_KEY).enqueue(
            object : Callback<MediaResponseList>{
                override fun onResponse(
                    call: Call<MediaResponseList>,
                    response: Response<MediaResponseList>
                ) {
                    if(response.isSuccessful){
                        println(response.body())
                        var mediaResponseList : List<MediaResponse> = response.body()!!.responseList
                        var mediaList: List<Media> = MediaMapper.fromMediaResponseToMedia(mediaResponseList)
                        fillGridLayout(mediaList);
                        println("mediaResponseList?.size" + mediaResponseList!!.size)
                        println("mediaList.length: " + mediaList.size)
                    }
                    else{
                        println("http status code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MediaResponseList>, t: Throwable) {
                    println("onFailure")
                    t.printStackTrace()
                }

            })
    }

    private fun fillGridLayout(mediaList: List<Media>){
        for(media in mediaList){
            var cardView = LayoutInflater.from(applicationContext)
                .inflate(R.layout.media_card, gridLayout, false)
            var posterMediaView = cardView.findViewById<ImageView>(R.id.media_poster)
            var titleMediaView = cardView.findViewById<TextView>(R.id.media_title)

            setTitleMediaView(titleMediaView, media)
            setMediaPoster(posterMediaView, media)

            gridLayout.addView(cardView)
        }
    }

    private fun setMediaPoster(posterMediaView: ImageView, media: Media){
        var posterPath : String? = ""
        if(Util.isItMovie(media)){
            var movie = media as Movie
            posterPath = movie.posterPath
        }
        else if(Util.isItShow(media)){
            var show = media as Show
            posterPath = show.posterPath
        }
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w342/$posterPath")
            .into(posterMediaView)
    }

    private fun setTitleMediaView(titleMediaView: TextView, media: Media){
        var mediaTitle : String? = ""
        if(Util.isItMovie(media)){
            var movie = media as Movie
            mediaTitle = movie.title
        }
        else if(Util.isItShow(media)){
            var show = media as Show
            mediaTitle = show.name
        }
        titleMediaView.text = mediaTitle
    }

//    fun multiSearch(query : String): Unit {
//        ApiService.getMediaService()?.multiSearch(API_KEY, query)
//        for(i in 0..19){
//            var cardView : CardView = LayoutInflater.from(this).inflate(
//                R.layout.media_card, this.parent as? ViewGroup,false) as CardView
//            gridLayout.addView(cardView)
//        }
//    }

}


