package com.example.moviesappkotlin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.activities.MediaDetailsActivity
import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.models.Person
import com.example.moviesappkotlin.models.Show
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants.Companion.API_KEY
import com.example.moviesappkotlin.util.Mapper
import com.example.moviesappkotlin.util.MyWindowMetrics
import com.example.moviesappkotlin.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class SearchFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var searchView: SearchView
    private lateinit var fragmentView: View
    private lateinit var progressBar: ProgressBar
    private lateinit var serializableMediaList: MutableList<Media>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView =  inflater.inflate(R.layout.fragment_search, container, false)
        setViews()
        loadMovies(savedInstanceState)
        setSearchView()
        return fragmentView
    }

    private fun loadMovies(savedInstanceState: Bundle?){
        if(savedInstanceState != null){
            if(serializableMediaList == null){
                serializableMediaList = savedInstanceState.getSerializable("serializableMediaList") as MutableList<Media>
            }
            fillGridLayout(serializableMediaList);
        }
        else{
            getMovies();
        }
    }

    private fun setViews(){
        progressBar = fragmentView.findViewById(R.id.progress_bar_search_fragment)
        serializableMediaList = mutableListOf()
        setGridLayout()
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
                        val mediaResponseList : List<MediaResponse> = response.body()!!.responseList
                        val mediaList: List<Media> = Mapper.fromMediaResponseToMedia(mediaResponseList)
                        serializableMediaList.addAll(mediaList)
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

            setOnClickListener(posterMediaView, mediaList[i])
            setOnClickListener(titleMediaView, mediaList[i])

            gridLayout.addView(cardView)
        }
    }

    fun setOnClickListener(view: View, media: Media){
        view.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(context, MediaDetailsActivity::class.java)
                intent.putExtra("media", media)
                startActivity(intent)
            }
        })
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
                        var mediaList: List<Media> = Mapper.fromMediaResponseToMedia(mediaResponseList)

                        // Extraindo filmes e shows de pessoas
                        mediaList = parseMedia(mediaList)
                        serializableMediaList.addAll(mediaList)
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

    fun setGridLayout(){
        gridLayout = fragmentView.findViewById(R.id.grid_layout)
        val myWindowMetrics = MyWindowMetrics(requireActivity())
        val widthWindowSizeClass = myWindowMetrics.getWidthSizeClass()
        val heightWindowSizeClass = myWindowMetrics.getHeightSizeClass()

       if(widthWindowSizeClass == MyWindowMetrics.WindowSizeClass.COMPACT)
           gridLayout.columnCount = 2
        else if(heightWindowSizeClass == MyWindowMetrics.WindowSizeClass.COMPACT)
           gridLayout.columnCount = 3
        else if(widthWindowSizeClass == MyWindowMetrics.WindowSizeClass.MEDIUM)
            gridLayout.columnCount = 4
        else if(heightWindowSizeClass == MyWindowMetrics.WindowSizeClass.MEDIUM &&
           widthWindowSizeClass == MyWindowMetrics.WindowSizeClass.EXPANDED)
            gridLayout.columnCount = 6
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putSerializable("serializableMediaList", serializableMediaList as Serializable)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

}