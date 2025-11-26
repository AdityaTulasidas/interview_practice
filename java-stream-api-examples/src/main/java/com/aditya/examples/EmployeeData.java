package com.aditya.examples;

import com.aditya.model.Employee;
import java.util.Arrays;
import java.util.List;

public class EmployeeData {
    
    public static List<Employee> getEmployees() {
        return Arrays.asList(
            new Employee(1L, "John", "IT", 75000, 28, "New York", Arrays.asList("Java", "Spring")),
            new Employee(2L, "Alice", "HR", 55000, 32, "Chicago", Arrays.asList("Communication", "Management")),
            new Employee(3L, "Bob", "IT", 85000, 35, "San Francisco", Arrays.asList("Python", "AWS")),
            new Employee(4L, "Carol", "Finance", 65000, 29, "Boston", Arrays.asList("Excel", "SQL")),
            new Employee(5L, "David", "IT", 95000, 40, "Seattle", Arrays.asList("Java", "Microservices")),
            new Employee(6L, "Eve", "Marketing", 50000, 26, "Austin", Arrays.asList("Digital Marketing", "Analytics")),
            new Employee(7L, "Frank", "IT", 70000, 31, "Denver", Arrays.asList("JavaScript", "React")),
            new Employee(8L, "Grace", "HR", 60000, 33, "Miami", Arrays.asList("Recruitment", "Training")),
            new Employee(9L, "Henry", "Finance", 80000, 37, "Portland", Arrays.asList("Accounting", "Finance")),
            new Employee(10L, "Ivy", "IT", 90000, 29, "Atlanta", Arrays.asList("DevOps", "Docker"))
        );
    }
}