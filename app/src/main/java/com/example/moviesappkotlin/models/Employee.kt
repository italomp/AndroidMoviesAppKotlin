package com.example.moviesappkotlin.models

import java.util.*

class Employee(
    var id: Long,
    var name: String,
    var department: String,
    var posterPath: String?){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other == null || javaClass != other.javaClass) return false;
        var employee: Employee = (other as Employee);
        return id == employee.id && name.equals(employee.name);
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }

    fun compareTo(obj: Any?): Int{
        var anotherEmp: Employee = (obj as Employee)
        return name.compareTo(anotherEmp.name)
    }

}