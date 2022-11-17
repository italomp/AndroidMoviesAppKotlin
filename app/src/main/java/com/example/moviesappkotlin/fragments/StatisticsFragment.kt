package com.example.moviesappkotlin.fragments

import android.annotation.SuppressLint
import android.widget.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var chartColorArray: IntArray
    private lateinit var customMarkerView: CustomMarkerView;
    /*
    private ProgressBar progressBar;
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

    private fun getMoviesRevenue(mediaResponseList: List<MediaResponse>, topTenRevenue: TopTen){
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
        dataSet.setColors(chartColorArray, fragmentView.context) // Adicionando cor às barras
        data.setDrawValues(false)                        // Removendo exibição de valores das barras
        data.barWidth = 0.8f                             // Espaço entre as barras

        addEventClickListenerOnTheChart()
        setChartLegends(barChart)

        barChart.data = data
        barChart.invalidate()                   // Fazer refresh
        barChart.animateY(500)      //Adicionando animação vertical às barras do gráfico
    }

    private fun addEventClickListenerOnTheChart(){
        barChart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                barChart.marker = customMarkerView
            }
            override fun onNothingSelected() {}
        })
    }

    private fun launchChartColorArray(){
        chartColorArray = intArrayOf(
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

    @SuppressLint("CutPasteId")
    private fun setChartLegends(barChart: BarChart){
        setLegendIconColors()

        val movieList = (topTenRevenue as TopTen).getTopTenRevenueDesc()

        val txtRowOneColOne: TextView = fragmentView.findViewById(R.id.txt_row_1_col_1)
        txtRowOneColOne.text = movieList[0].title

        val txtRowOneColTwo: TextView = fragmentView.findViewById(R.id.txt_row_1_col_2)
        txtRowOneColTwo.text = movieList[1].title

        val txtTwoOneColOne: TextView = fragmentView.findViewById(R.id.txt_row_2_col_1)
        txtTwoOneColOne.text = movieList[2].title

        val txtTwoOneColTwo: TextView = fragmentView.findViewById(R.id.txt_row_2_col_2)
        txtTwoOneColTwo.text = movieList[3].title

        val txtRowThreeColOne: TextView = fragmentView.findViewById(R.id.txt_row_3_col_1)
        txtRowThreeColOne.text = movieList[4].title

        val txtRowThreeColTwo: TextView = fragmentView.findViewById(R.id.txt_row_3_col_2)
        txtRowThreeColTwo.text = movieList[5].title

        val txtRowFourColOne: TextView = fragmentView.findViewById(R.id.txt_row_4_col_1)
        txtRowFourColOne.text = movieList[6].title

        val txtRowFourColTwo: TextView = fragmentView.findViewById(R.id.txt_row_4_col_2)
        txtRowFourColTwo.text = movieList[7].title

        val txtRowFiveColOne: TextView = fragmentView.findViewById(R.id.txt_row_5_col_1)
        txtRowFiveColOne.text = movieList[8].title

        val txtRowFiveColTwo: TextView = fragmentView.findViewById(R.id.txt_row_5_col_2)
        txtRowFiveColTwo.text = movieList[9].title
    }


    private fun setLegendIconColors(){
        val BAR_COLOR_1 = 0xFFF21F26
        val BAR_COLOR_2 = 0xFFF3D915
        val BAR_COLOR_3 = 0xFFBA5252
        val BAR_COLOR_5 = 0xFFD4D9A1
        val BAR_COLOR_4 = 0xFFC09491
        val BAR_COLOR_6 = 0xFFF8EDD1
        val BAR_COLOR_7 = 0xFFD88A8A
        val BAR_COLOR_8 = 0xFF474843
        val BAR_COLOR_9 = 0xFF9D9D93
        val BAR_COLOR_10 = 0xFFC5CFC6

        val imgRowOneColOne: ImageView = this.fragmentView.findViewById(R.id.img_row_1_col_1)
        imgRowOneColOne.setColorFilter(BAR_COLOR_1.toInt())

        val imgRowOneColTwo: ImageView = this.fragmentView.findViewById(R.id.img_row_1_col_2)
        imgRowOneColTwo.setColorFilter(BAR_COLOR_2.toInt())

        val imgRowTwoColOne: ImageView = this.fragmentView.findViewById(R.id.img_row_2_col_1)
        imgRowTwoColOne.setColorFilter(BAR_COLOR_3.toInt())

        val imgRowTwoColTwo: ImageView = this.fragmentView.findViewById(R.id.img_row_2_col_2)
        imgRowTwoColTwo.setColorFilter(BAR_COLOR_4.toInt())

        val imgRowThreeColOne: ImageView = this.fragmentView.findViewById(R.id.img_row_3_col_1)
        imgRowThreeColOne.setColorFilter(BAR_COLOR_5.toInt())

        val imgRowThreeColTwo: ImageView = this.fragmentView.findViewById(R.id.img_row_3_col_2)
        imgRowThreeColTwo.setColorFilter(BAR_COLOR_6.toInt())

        val imgRowFourColOne: ImageView = this.fragmentView.findViewById(R.id.img_row_4_col_1)
        imgRowFourColOne.setColorFilter(BAR_COLOR_7.toInt())

        val imgRowFourColTwo: ImageView = this.fragmentView.findViewById(R.id.img_row_4_col_2)
        imgRowFourColTwo.setColorFilter(BAR_COLOR_8.toInt())

        val imgRowFiveColOne: ImageView = this.fragmentView.findViewById(R.id.img_row_5_col_1)
        imgRowFiveColOne.setColorFilter(BAR_COLOR_9.toInt())

        val imgRowFiveColTwo: ImageView = this.fragmentView.findViewById(R.id.img_row_5_col_2)
        imgRowFiveColTwo.setColorFilter(BAR_COLOR_10.toInt())
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


