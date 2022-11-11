package com.example.moviesappkotlin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TableLayout
import com.example.moviesappkotlin.R
import com.github.mikephil.charting.charts.BarChart

class StatisticsFragment : Fragment() {
    private lateinit var fragmentView: View
    private val SORT_TYPE = "revenue.desc"
    private lateinit var spinnerYear: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<CharSequence>
    private lateinit var barChart: BarChart
    private lateinit var chartLegends: TableLayout
    /*
    private Observable topTenRevenue;
    private Context context;
    private ProgressBar progressBar;
    private int[] chartColorArray = new int[10];
    private CustomMarkerView customMarkerView;
    * */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_statistics, container, false)

        setViewsAndVariables()


        return fragmentView
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
//
//                // limpar a listagem anterior
//                ((TopTen) topTenRevenue).setTopTen(new ArrayList<>());
//
                var year = spinnerYear.getSelectedItem().toString() as Int
//                getMoviesByYear(year, SORT_TYPE, topTenRevenue as TopTen)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getMoviesByYear(year: Int, sortType: String){
        
    }

    /*
    public void setViewsAndVariables(View view){
        this.context = view.getContext();
        this.barChart = view.findViewById(R.id.bar_chart);
        this.topTenRevenue = new TopTen(SORT_BY_REVENUE);
        this.topTenRevenue.addObserver(this);
        this.progressBar = view.findViewById(R.id.progress_bar_statistics_fragment);
        this.customMarkerView = new CustomMarkerView(getContext(), R.layout.marker_view);
        this.chartLegends = view.findViewById(R.id.chart_legends);
    }
    */
    private fun setViewsAndVariables(){
        barChart = fragmentView.findViewById(R.id.bar_chart)
        chartLegends = fragmentView.findViewById(R.id.chart_legends)
    }
}