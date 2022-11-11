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

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(SearchFragment())

        bottomNavBar = findViewById(R.id.bottom_nav_bar)
        bottomNavBar.setOnItemSelectedListener { item ->
            item.isChecked = true
            if (item.itemId == R.id.bottom_nav_home_icon)
                replaceFragment(SearchFragment())
            else if (item.itemId == R.id.bottom_nav_chart_icon)
                replaceFragment(StatisticsFragment())
            false
        }
    }

    private fun replaceFragment(fragment: Fragment){
        var fragmentManager: FragmentManager = supportFragmentManager
        var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}


