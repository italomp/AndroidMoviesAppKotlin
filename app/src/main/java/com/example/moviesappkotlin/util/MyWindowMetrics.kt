package com.example.moviesappkotlin.util

import android.app.Activity
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator

class MyWindowMetrics(private val activity: Activity) {

    fun getWidthSizeClass(): WindowSizeClass {
        val windowMetrics: WindowMetrics = WindowMetricsCalculator
            .getOrCreate().computeCurrentWindowMetrics(activity)
        val density: Float = activity.resources.displayMetrics.density
        val widthDp: Float = windowMetrics.bounds.width() / density
        return if (widthDp < 600f)
            WindowSizeClass.COMPACT
        else if (widthDp < 840f)
            WindowSizeClass.MEDIUM
        else
            WindowSizeClass.EXPANDED
    }

    fun getHeightSizeClass(): WindowSizeClass{
        val windowMetrics: WindowMetrics = WindowMetricsCalculator
            .getOrCreate().computeCurrentWindowMetrics(activity)
        val density: Float = activity.resources.displayMetrics.density
        val heightDp: Float = windowMetrics.bounds.height() / density
        return if(heightDp < 480f)
            WindowSizeClass.COMPACT
        else if(heightDp < 900)
            WindowSizeClass.MEDIUM
        else
            WindowSizeClass.EXPANDED
    }

    enum class WindowSizeClass {
        COMPACT, MEDIUM, EXPANDED
    }
}