package com.example.moviesappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.SearchView
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants
import com.example.moviesappkotlin.util.Constants.Companion.API_KEY
import com.example.moviesappkotlin.util.MediaMapper
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                        var mediaList = MediaMapper.fromMediaResponseToMedia(mediaResponseList)

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

//    private fun fillGridLayout(){
//
//    }

//    fun multiSearch(query : String): Unit {
//        ApiService.getMediaService()?.multiSearch(API_KEY, query)
//        for(i in 0..19){
//            var cardView : CardView = LayoutInflater.from(this).inflate(
//                R.layout.media_card, this.parent as? ViewGroup,false) as CardView
//            gridLayout.addView(cardView)
//        }
//    }

}