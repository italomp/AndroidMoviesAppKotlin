package com.example.moviesappkotlin.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.models.Movie
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView : MarkerView{
    var textView: TextView
    var xAxisValue: Int

    constructor(context: Context?, layoutResource: Int) :
    super(context, layoutResource){
        this.textView = findViewById(R.id.marker_view)
        this.xAxisValue = layoutResource
    }

    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry, highlight: Highlight){
        xAxisValue = e.x.toInt()
        val title: String? = (e.data as Movie).title
        val revenue: String = "U$ ${(e.data as Movie).revenue} ."
        textView.text = "$title - $revenue"
        super.refreshContent(e, highlight) // Faz o marker ajustar-se ao tamanho do texto
    }

    /**
     * Retorna um MMPointF que posicionar o marker.
     * O primeiro parâmetro desse objeto posiciona o marker horizontalmente e o segundo posiciona
     * verticalmetne.
     *
     * -(getWidth() / 2), no primeiro parâmetro, centraliza o marker horizontalmente sobre a barra.
     * -getHeight(), posiciona o marker sobre a barra.
     *
     * Se a barra selecionada, for alguma da primeira (índice 0) à sétima (índice 6), centralizo o
     * marker horizontalmente sobre a barra.
     * Se for da sétima em diante, alinho o marker à esquerda da barra.
     */
    override fun getOffset(): MPPointF {
        if(xAxisValue < 7)
            return MPPointF(-(width.toFloat() / 2), -height.toFloat());
        return MPPointF(-width.toFloat(), -height.toFloat());
    }
}