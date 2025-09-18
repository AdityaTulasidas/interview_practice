package com.interview.practice;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class service {

    public void processData(ArrayList<String> data) {
        // Process the data

    }

    public void exampleSortEmployees() {
        List<Employee> employees = List.of(
                new Employee(1, "Alice", "HR", 50000),
                new Employee(2, "Bob", "IT", 70000),
                new Employee(3, "Charlie", "HR", 60000),
                new Employee(4, "David", "IT", 90000),
                new Employee(5, "Eve", "Finance", 80000)
        );

        // Group by department and get the highest salaried employee in each department
        Map<String, Employee> highestPaidByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                                opt -> opt.orElse(null)
                        )
                ));

        highestPaidByDept.forEach((dept, emp) -> {
            System.out.println("Department: " + dept + ", Highest Paid: " + emp);
        });
    }
}
