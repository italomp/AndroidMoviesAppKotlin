package com.example.moviesappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.adapters.CrewListItemViewPagerAdapter
import com.example.moviesappkotlin.models.Crew
import com.example.moviesappkotlin.models.Media
import com.example.moviesappkotlin.models.Person
import com.example.moviesappkotlin.models.Show
import com.example.moviesappkotlin.responses.CrewResponse
import com.example.moviesappkotlin.responses.MediaDetailsResponse
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants.Companion.API_KEY
import com.example.moviesappkotlin.util.Mapper
import com.example.moviesappkotlin.util.Util
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class MediaDetailsActivity : AppCompatActivity() {
    lateinit var media: Media
    lateinit var posterView: ShapeableImageView
    lateinit var titleView: TextView
    lateinit var noteAverageView: TextView
    lateinit var overviewView: TextView
    lateinit var layoutCrewList: LinearLayout
    lateinit var progressBar: ProgressBar
    lateinit var mediaDetailsScroll: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_details)
        getViewsReferences()

        Util.showProgressBarAndHiddenView(progressBar, arrayOf(mediaDetailsScroll))

        receivingMedia()
        getMediaDetails()
    }

    fun getViewsReferences(){
        posterView = findViewById(R.id.details_media_poster);
        titleView = findViewById(R.id.details_media_title);
        noteAverageView = findViewById(R.id.vote_average);
        overviewView = findViewById(R.id.details_media_overview);
        progressBar = findViewById(R.id.load_screen);
        mediaDetailsScroll = findViewById(R.id.media_details_scroll);
        layoutCrewList = findViewById(R.id.crew_list);
    }

    fun receivingMedia(){
        val receivedData: Bundle? = intent.extras
        media = receivedData!!.getSerializable("media") as Media
    }

    fun getMediaDetails(){
        if(Util.isItMovie(media)){
            ApiService.movieService.getMovieDetails(media.id!!, API_KEY).enqueue(
                object : Callback<MediaDetailsResponse>{
                    override fun onResponse(
                        call: Call<MediaDetailsResponse>,
                        response: Response<MediaDetailsResponse>
                    ) {
                        if(response.isSuccessful){
                            setDetailViews(response)
                            getCrew(media)
                        }
                        else{
                            Util.hiddenProgressBar(progressBar)
                            Util.showMessage(baseContext, "HTTP Status Code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<MediaDetailsResponse>, t: Throwable) {
                        Util.hiddenProgressBar(progressBar)
                        Util.showMessage(baseContext, "Falha de Comunicação")
                        t.printStackTrace()
                    }
                }
            )
        }
        else if(Util.isItShow(media)){
            ApiService.showService.getShowDetails(media.id!!, API_KEY).enqueue(
                object: Callback<MediaDetailsResponse>{
                    override fun onResponse(
                        call: Call<MediaDetailsResponse>,
                        response: Response<MediaDetailsResponse>
                    ) {
                        if (response.isSuccessful) {
                            setDetailViews(response)
                            getCrew(media)
                        }
                        else{
                            Util.hiddenProgressBar(progressBar)
                            Util.showMessage(baseContext, "HTTP status code: ${response.code()}");
                        }
                    }

                    override fun onFailure(call: Call<MediaDetailsResponse>, t: Throwable) {
                        Util.hiddenProgressBar(progressBar)
                        Util.showMessage(baseContext, "Falha de Comunicação");
                        t.printStackTrace()
                    }
                }
            )
        }
    }

    fun setDetailViews(response: Response<MediaDetailsResponse>){
        if(response.body() == null)
            return

        val mediaTitle: String = if(Util.isItMovie(media))
            response.body()!!.title!!
        else if(Util.isItShow(media))
            (response.body() as Show).name!!
        else
            (response.body() as Person).title!!
        val mediaOverview: String? = response.body()!!.overview
        val postPath: String? = response.body()!!.posterPath
        val noteAverage: String = String.format("%.1f", response.body()!!.voteAverage)

        titleView.text = mediaTitle
        noteAverageView.text = "Avaliação do usuário: ${noteAverage}%"
        overviewView.text = mediaOverview
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w342/$postPath")
            .into(this.posterView)
    }

    fun getCrew(media: Media){
        if(Util.isItMovie(media)){
            ApiService.movieService.getCreditsByMovie(media.id!!, API_KEY).enqueue(
                object: Callback<CrewResponse>{
                    override fun onResponse(
                        call: Call<CrewResponse>,
                        response: Response<CrewResponse>
                    ) {
                        if (response.isSuccessful){
                            val crew: Crew = Mapper.fromCrewResponseToCrew(response.body()!!);
                            setCrewList(crew);
                        }

                        // HTTP status code diferente de 200 a 299
                        else
                            Util.showMessage(baseContext,"HTTP status code: ${response.code()}")

                        // Desbloqueando a tela principal
                        Util.hiddenProgressBarAndShowView(progressBar, arrayOf(mediaDetailsScroll))
                    }

                    override fun onFailure(call: Call<CrewResponse>, t: Throwable) {
                        Util.hiddenProgressBar(progressBar)
                        Util.showMessage(baseContext, "Falha de comunicação")
                        t.printStackTrace()
                    }
                }
            )
        }
        else if(Util.isItShow(media)){
            ApiService.showService.getCreditsByShow(media.id!!, API_KEY).enqueue(
                object: Callback<CrewResponse>{
                    override fun onResponse(
                        call: Call<CrewResponse>,
                        response: Response<CrewResponse>
                    ) {
                        if(response.isSuccessful){
                            var crew = Mapper.fromCrewResponseToCrew(response.body()!!);
                            setCrewList(crew);
                        }

                        else
                            Util.showMessage(baseContext, "HTTP status code: ${response.code()}");

                        Util.hiddenProgressBarAndShowView(progressBar, arrayOf(mediaDetailsScroll))
                    }

                    override fun onFailure(call: Call<CrewResponse>, t: Throwable) {
                        Util.hiddenProgressBar(progressBar)
                        Util.showMessage(baseContext, "Falha de comunicação")
                        t.printStackTrace()
                    }
                }
            )
        }
    }

    fun setCrewList(crew: Crew){
        val departments = crew.getAllDepartments()
        layoutCrewList = findViewById(R.id.crew_list)

        for(department in departments){
            val departmentSectionView = LayoutInflater
                .from(applicationContext)
                .inflate(R.layout.department_crew_section, layoutCrewList, false)
            val departmentNameView = departmentSectionView.findViewById<TextView>(R.id.department_name)
            val employeeRecyclerView = departmentSectionView.findViewById<RecyclerView>(R.id.employees_list)
            val layoutManager = LinearLayoutManager(baseContext)
            val employeeListFromCurrentDepartment = crew.getEmployeesByDepartment(department)
            val adapter = CrewListItemViewPagerAdapter(employeeListFromCurrentDepartment)
            adapter.hasStableIds()
            employeeRecyclerView.setHasFixedSize(true)
            layoutManager.orientation = RecyclerView.HORIZONTAL
            employeeRecyclerView.layoutManager = layoutManager

            departmentNameView.text = department
            employeeRecyclerView.adapter = adapter

            layoutCrewList.addView(departmentSectionView)
        }
    }


}