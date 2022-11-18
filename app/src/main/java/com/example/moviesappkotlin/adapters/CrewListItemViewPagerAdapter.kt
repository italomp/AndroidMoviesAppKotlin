package com.example.moviesappkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesappkotlin.R
import com.example.moviesappkotlin.models.Employee
import com.example.moviesappkotlin.util.Constants.Companion.API_IMAGES_URL
import com.squareup.picasso.Picasso

class CrewListItemViewPagerAdapter(
    val employeeList: List<Employee>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.crew_employee_card, parent, false)
        return EmployeeCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val employee = employeeList[position]
        val employeePhotoView = (holder as EmployeeCardViewHolder).employeePhoto
        val employeeNameView = (holder as EmployeeCardViewHolder).employeeName
        val url = API_IMAGES_URL + employee.posterPath

        Picasso.get().load(url).error(R.drawable.employee_default_image).into(employeePhotoView)
        employeeNameView.text = employee.name
    }

    override fun getItemCount(): Int = employeeList.size

    class EmployeeCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var employeePhoto: ImageView
        var employeeName: TextView

        init {
            val employeeCard = itemView.findViewById<CardView>(R.id.crew_employee_card)
            employeePhoto = employeeCard.findViewById(R.id.employee_photo)
            employeeName = employeeCard.findViewById(R.id.employee_name)
        }
    }

}