package com.example.moviesappkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.models.Person
import com.example.moviesappkotlin.models.Show
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants.Companion.API_KEY
import com.example.moviesappkotlin.util.MediaMapper
import com.example.moviesappkotlin.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var searchView: SearchView
    private lateinit var fragmentView: View
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView =  inflater.inflate(R.layout.fragment_search, container, false)
        setViews()
        getMovies()
        return fragmentView
    }

    private fun setViews(){
        gridLayout = fragmentView.findViewById(R.id.grid_layout)
        progressBar = fragmentView.findViewById(R.id.progress_bar_search_fragment)
        setSearchView()
    }

    private fun setSearchView(){
        searchView = fragmentView.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null)
                    return false
                multiSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println(newText)
                if (newText == null)
                    return false
                multiSearch(newText)
                return false
            }
        })
    }

    private fun getMovies(){
        val scrollView: ScrollView = fragmentView.findViewById(R.id.scroll_view_search_fragment)
        Util.showProgressBarAndHiddenView(progressBar, arrayOf(scrollView))
        ApiService.movieService.getMovies(API_KEY).enqueue(
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
                        Util.hiddenProgressBarAndShowView(progressBar, arrayOf(scrollView))
                    }
                    else{
                        Util.showMessage(fragmentView.context, "Http status code: ${response.code()}.")
                    }
                }

                override fun onFailure(call: Call<MediaResponseList>, t: Throwable) {
                    println("executando onFailure")
                    Util.showMessage(fragmentView.context, "Requisição de filmes falhou.")
                    t.printStackTrace()
                }
            })
    }

    private fun fillGridLayout(mediaList: List<Media>){
        for(i in mediaList.indices){
            val cardView = LayoutInflater.from(fragmentView.context)
                .inflate(R.layout.media_card, gridLayout, false)
            val posterMediaView = cardView.findViewById<ImageView>(R.id.media_poster)
            val titleMediaView = cardView.findViewById<TextView>(R.id.media_title)

            setMediaTitleView(titleMediaView, mediaList[i])
            setMediaPosterView(posterMediaView, mediaList[i])

            gridLayout.addView(cardView)
        }
    }

    private fun setMediaPosterView(posterMediaView: ImageView, media: Media){
        var posterPath : String? = ""
        if(Util.isItMovie(media))
            posterPath = (media as Movie).posterPath
        else if(Util.isItShow(media))
            posterPath = (media as Show).posterPath
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w342/$posterPath")
            .into(posterMediaView)
    }

    private fun setMediaTitleView(titleMediaView: TextView, media: Media){
        var mediaTitle : String? = ""
        if(Util.isItMovie(media)){
            val movie = media as Movie
            mediaTitle = movie.title
        }
        else if(Util.isItShow(media)){
            val show = media as Show
            mediaTitle = show.name
        }
        titleMediaView.text = mediaTitle
    }

    private fun multiSearch(query: String){
        ApiService.mediaService.multiSearch(API_KEY, query).enqueue(
            object: Callback<MediaResponseList>{
                override fun onResponse(
                    call: Call<MediaResponseList>,
                    response: Response<MediaResponseList>
                ) {
                    if(response.isSuccessful){
                        // Removendo itens listados anteriormente
                        gridLayout.removeAllViews();

                        val mediaResponseList : List<MediaResponse> = response.body()!!.responseList
                        var mediaList: List<Media> = MediaMapper.fromMediaResponseToMedia(mediaResponseList)

                        // Extraindo filmes e shows de pessoas
                        mediaList = parseMedia(mediaList)
                        renderingMediasOrNotFoundMessage(mediaList)
                    }
                    else{
                        Util.showMessage(fragmentView.context, "Http status code: ${response.code()}.")
                    }
                }

                override fun onFailure(call: Call<MediaResponseList>, t: Throwable) {
                    Util.showMessage(fragmentView.context, "Requisição de busca falhou.")
                }
            })
    }


    private fun parseMedia(mediaList: List<Media>): List<Media>{
        val resultList : MutableList<Media> = mutableListOf()
        val mediaSet: MutableSet<Media> = mutableSetOf()

        for(media in mediaList){
            if(Util.isItMovie(media) || Util.isItShow(media)){
                mediaSet.add(media)
            }
            else{
                val moviesAndShows = (media as Person).moviesAndShows
                if (moviesAndShows != null)
                    mediaSet.addAll(moviesAndShows)
            }
        }
        resultList.addAll(mediaSet)
        return resultList
    }

    private fun renderingMediasOrNotFoundMessage(mediaList: List<Media>){
        if(mediaList.isNotEmpty())
            fillGridLayout(mediaList)
        else
            Util.showMessage(fragmentView.context, "Nenhuma mídia foi encontrada")
    }
}