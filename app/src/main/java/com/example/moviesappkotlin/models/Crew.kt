package com.example.moviesappkotlin.models

class Crew(var employeeList: List<Employee>) {
    fun getAllDepartments(): List<String>{
        var departmentSet: MutableSet<String> = mutableSetOf()
        var departmentList: MutableList<String> = mutableListOf()

        for(emp in employeeList){
            departmentSet.add(emp.department)
        }

        departmentList.addAll(departmentSet)
        departmentList.sort() // Esse sort tá diferente do sorte do projeto em java. Não recebe comparator
        return departmentList
    }

    fun getEmployeesByDepartment(department: String): MutableList<Employee> {
        var employeeSet: MutableSet<Employee> = mutableSetOf()
        var employeeListResult: MutableList<Employee> = mutableListOf()

        for(emp in employeeList){
            if(department == emp.department){
                employeeSet.add(emp)
            }
        }

        employeeListResult.addAll(employeeSet)
        employeeListResult.sortBy{ it.name }
        return employeeListResult
    }

}


