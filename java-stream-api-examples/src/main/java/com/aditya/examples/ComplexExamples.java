package com.aditya.examples;

import com.aditya.model.Employee;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/*

    ### Complex Scenarios
    16. Find employees who earn more than average salary
    17. Get distinct cities from a list of employees, sorted alphabetically
    18. Calculate total sales by product category
    19. Find the second highest salary
    20. Get names of employees in "IT" department with salary > 50000

 */
public class ComplexExamples {
//        16. Find employees who earn more than average salary
    public static void employeesWhoEarnMoreThanAvg(){
        OptionalDouble avgsal = EmployeeData.getEmployees().stream().mapToDouble(Employee::getSalary).average();
        double avgSal = avgsal.getAsDouble();
        EmployeeData.getEmployees().stream().filter(emp -> emp.getSalary() > avgSal).collect(Collectors.toList()).forEach(System.out::println);
    }
//        17. Get distinct cities from a list of employees, sorted alphabetically

    public static void getEmployeesFromDiffCity() {
        EmployeeData.getEmployees().stream().map(Employee::getCity).distinct().sorted().collect(Collectors.toList());










                //.distinct().sorted().collect(Collectors.toList()).forEach(System.out::println);
    }
}
