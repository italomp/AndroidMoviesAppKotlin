package com.example.moviesappkotlin.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.fragments.SearchFragment
import com.example.moviesappkotlin.fragments.StatisticsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var searchFragment: SearchFragment
    private lateinit var statisticsFragment: StatisticsFragment
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragments(savedInstanceState)
        replaceFragment(currentFragment)
        setBottomNavigationView()
    }

    private fun loadFragments(savedInstanceState: Bundle?){
        if(savedInstanceState != null) {
            searchFragment = savedInstanceState.getSerializable("searchFragment") as SearchFragment
            statisticsFragment = savedInstanceState.getSerializable("statisticsFragment") as StatisticsFragment
            currentFragment = savedInstanceState.getSerializable("currentFragment") as Fragment
        }
        else{
            searchFragment = SearchFragment()
            currentFragment = searchFragment
            statisticsFragment = StatisticsFragment()
        }
    }

    private fun setBottomNavigationView(){
        bottomNavView = findViewById(R.id.bottom_nav_bar)
        bottomNavView.setOnItemSelectedListener { item ->
            item.isChecked = true
            if (item.itemId == R.id.bottom_nav_home_icon){
                currentFragment = searchFragment
                replaceFragment(SearchFragment())
            }
            else if (item.itemId == R.id.bottom_nav_chart_icon) {
                currentFragment = statisticsFragment
                replaceFragment(StatisticsFragment())
            }
            false
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState);
        outState.putSerializable("searchFragment", searchFragment as Serializable)
        outState.putSerializable("statisticsFragment", statisticsFragment as Serializable)
        outState.putSerializable("currentFragment", currentFragment as Serializable)
    }
}


