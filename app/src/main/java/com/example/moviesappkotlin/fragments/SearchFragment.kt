package com.example.moviesappkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.models.Show
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants
import com.example.moviesappkotlin.util.MediaMapper
import com.example.moviesappkotlin.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var searchView: SearchView
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =  inflater.inflate(R.layout.fragment_search, container, false)
        setViews()
        getMovies()
        // Inflate the layout for this fragment
        return fragmentView
    }

    private fun setViews(){
        gridLayout = fragmentView.findViewById(R.id.grid_layout)
        setSearchView()
    }

    private fun setSearchView(){
        searchView = fragmentView.findViewById(R.id.search_view)
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
        ApiService.movieService.getMovies(Constants.API_KEY).enqueue(
            object : Callback<MediaResponseList> {
                override fun onResponse(
                    call: Call<MediaResponseList>,
                    response: Response<MediaResponseList>
                ) {
                    if(response.isSuccessful){
                        println(response.body())
                        var mediaResponseList : List<MediaResponse> = response.body()!!.responseList
                        var mediaList: List<Media> = MediaMapper.fromMediaResponseToMedia(mediaResponseList)
                        fillGridLayout(mediaList);
                    }
                    else{
                        Util.showMessage(view!!.context, "Http status code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MediaResponseList>, t: Throwable) {
                    Util.showMessage(view!!.context, "Requisição de filmes falhou")
                    t.printStackTrace()
                }
            })
    }

    private fun fillGridLayout(mediaList: List<Media>){
        for(media in mediaList){
            var cardView = LayoutInflater.from(fragmentView.context)
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
}