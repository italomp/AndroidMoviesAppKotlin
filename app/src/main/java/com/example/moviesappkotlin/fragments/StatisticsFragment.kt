package com.example.moviesappkotlin.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.models.Movie
import com.example.moviesappkotlin.responses.MediaDetailsResponse
import com.example.moviesappkotlin.responses.MediaResponse
import com.example.moviesappkotlin.responses.MediaResponseList
import com.example.moviesappkotlin.services.ApiService
import com.example.moviesappkotlin.util.Constants.Companion.API_KEY
import com.example.moviesappkotlin.util.CustomMarkerView
import com.example.moviesappkotlin.util.MediaMapper
import com.example.moviesappkotlin.util.Util
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.util.*
import java.util.stream.Collectors

@RequiresApi(Build.VERSION_CODES.N)
class StatisticsFragment : Fragment(), Observer {
    private lateinit var fragmentView: View
    private val SORT_TYPE = "revenue.desc"
    private lateinit var spinnerYear: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<CharSequence>
    private lateinit var barChart: BarChart
    private lateinit var chartLegends: TableLayout
    private lateinit var topTenRevenue: Observable
    private lateinit var chartColorArray: Array<Int>
    private lateinit var customMarkerView: CustomMarkerView;
    /*
    private Context context;
    private ProgressBar progressBar;
    private int[] chartColorArray = new int[10];

    * */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_statistics, container, false)

        setViewsAndVariables()
        setSpinnerYear()
        var year = spinnerYear.selectedItem.toString().toInt()
        loadTopTenRevenue(savedInstanceState, year)
        return fragmentView
    }

    private fun loadTopTenRevenue(savedInstanceState: Bundle?, year: Int){
        if(savedInstanceState != null)
            println("deveria resgatar o bundle")
        else{
            getMoviesByYear(year, SORT_TYPE, topTenRevenue as TopTen)
        }

    }

    private fun getMoviesByYear(year: Int, sortType: String, topTenRevenue: TopTen){
        ApiService.movieService.getMoviesByYear(API_KEY, year, sortType).enqueue(
            object: Callback<MediaResponseList> {
                override fun onResponse(
                    call: Call<MediaResponseList>,
                    response: Response<MediaResponseList>
                ) {
                    if(response.isSuccessful){
                        var mediaResponseList: List<MediaResponse>  = response.body()!!.responseList
                        getMoviesRevenue(mediaResponseList, topTenRevenue)
                    }
                    else{
                        Util.showMessage(
                            fragmentView.context,
                            "Status code: " + response.code())
                    }
                }

                override fun onFailure(call: Call<MediaResponseList>, t: Throwable) {
                    Util.showMessage(
                        fragmentView.context,
                        "Falha ao pesquisar filmes do ano: $year")
                    t.printStackTrace()
                }
            }
        )
    }

    fun getMoviesRevenue(mediaResponseList: List<MediaResponse>, topTenRevenue: TopTen){
        var totalAmount = 10
        for(i in 0 until totalAmount){
            if( i == totalAmount)
                break

            var current = mediaResponseList[i]
            ApiService.movieService.getMovieDetails(current.id, API_KEY).enqueue(
                object: Callback<MediaDetailsResponse>{
                    override fun onResponse(
                        call: Call<MediaDetailsResponse>,
                        response: Response<MediaDetailsResponse>
                    ) {
                        if(response.isSuccessful){
                            val mediaDetailsResponse = response.body()
                            val movie = MediaMapper.fromMediaDetailsToMovie(mediaDetailsResponse!!)
                            topTenRevenue.addMovie(movie)
                        }
                        else{
                            Util.showMessage(fragmentView.context,
                                "Http Status Code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<MediaDetailsResponse>, t: Throwable) {
                        Util.showMessage(fragmentView.context,
                            "Falha de Comunicação")
                    }
                }
            )
        }
    }

    private fun setSpinnerYear(){
        spinnerYear = fragmentView.findViewById(R.id.spinner_year);
        spinnerAdapter = ArrayAdapter.createFromResource(
            fragmentView.context, R.array.select_year, android.R.layout.simple_spinner_dropdown_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = spinnerAdapter
        spinnerYear.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
//                Util.showProgressBarAndHiddenView(progressBar, new View[]{barChart, chartLegends});

                // limpar a listagem anterior
                (topTenRevenue as TopTen).topTen = mutableListOf()

                val year = spinnerYear.selectedItem.toString().toInt()
                getMoviesByYear(year, SORT_TYPE, topTenRevenue as TopTen)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /*
    public void setViewsAndVariables(View view){
        this.progressBar = view.findViewById(R.id.progress_bar_statistics_fragment);
        this.chartLegends = view.findViewById(R.id.chart_legends);
    }
    */
    private fun setViewsAndVariables(){
        barChart = fragmentView.findViewById(R.id.bar_chart)
        chartLegends = fragmentView.findViewById(R.id.chart_legends)
        topTenRevenue = TopTen(SORT_TYPE)
        topTenRevenue.addObserver(this)
        customMarkerView = CustomMarkerView(fragmentView.context, R.layout.marker_view);
    }

    override fun update(observable: Observable?, obj: Any?) {
        if(observable is TopTen){
            var topTenList: List<Movie> = obj as List<Movie>
            var entries: MutableList<BarEntry> = mutableListOf()
            setEntriesToBarChar(topTenList, entries)
            setBarChart(entries)
        }
    }

    private fun setEntriesToBarChar(topTenList: List<Movie>, entries: MutableList<BarEntry>){
        for (i in topTenList.indices) {
            val mv: Movie = topTenList[i]
            val barValue = mv.revenue!!
            entries.add(BarEntry(i.toFloat(), barValue.toFloat() , mv))
        }
    }

    private fun setBarChart(entries: List<BarEntry>){
        val dataSet: BarDataSet = BarDataSet(entries, "")
        val data: BarData = BarData(dataSet)

        barChart.description.isEnabled = false // Removendo descrição
        barChart.axisRight.isEnabled = false   // Removendo valor à direita do eixo Y
        barChart.xAxis.setDrawLabels(false)    // Removendo valor à direita do eixo X
        barChart.setFitBars(true)              // Ponto as barras centralizadas aos pontos de x

        // Formatando valores às esqueda do eixo Y
        var yAxisLeft: YAxis = barChart.axisLeft
        yAxisLeft.valueFormatter = LargeValueFormatter()

        launchChartColorArray()
        dataSet.colors = chartColorArray.toMutableList() // Adicionando cor às barras
        data.setDrawValues(false)                        // Removendo exibição de valores das barras
        data.barWidth = 0.8f                             // Espaço entre as barras

        addEventClickListenerOnTheChart()
//        setChartLegends(barChart)

        barChart.data = data
        barChart.invalidate()                   // Fazer refresh
        barChart.animateY(500)      //Adicionando animação vertical às barras do gráfico
    }

    fun addEventClickListenerOnTheChart(){
        barChart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                barChart.marker = customMarkerView
            }
            override fun onNothingSelected() {}
        })
    }

    private fun launchChartColorArray(){
        chartColorArray = arrayOf(
            R.color.bar_color_1,
            R.color.bar_color_2,
            R.color.bar_color_3,
            R.color.bar_color_4,
            R.color.bar_color_5,
            R.color.bar_color_6,
            R.color.bar_color_7,
            R.color.bar_color_8,
            R.color.bar_color_9,
            R.color.bar_color_10
        )
    }

    class TopTen(private val orderBy: String) : Observable(), Serializable{
        var topTen: MutableList<Movie> = mutableListOf()

        fun addMovie(movie: Movie){
            this.topTen.add(movie)
            if(topTen.size == 10){
                setChanged()
                notifyObservers(getTopTenRevenueDesc())
            }
        }

        fun getTopTenRevenueDesc(): List<Movie> {
            return topTen.stream().sorted { movie1, movie2 ->
                if (movie1!!.revenue!! < movie2!!.revenue!!)
                    1
                else if (movie1.revenue == movie2.revenue)
                    0
                else
                    -1
            }.collect(Collectors.toList())
        }
    }
}