package com.aditya.examples;

import com.aditya.model.Employee;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.out;

/*
    ### Advanced Operations
    11. Flatten a list of lists into a single list using flatMap()
    12. Group employees by department using collect(groupingBy())
    13. Find the average salary of employees
    14. Get the top 3 highest paid employees
    15. Convert a list to a Map where key=name, value=age
 */
public class AdvancedStreamExamples {
//        11. Flatten a list of lists into a single list using flatMap()
    public static void flattenLists() {
        List<List<Integer>> listOfLists = List.of(
            List.of(1, 2, 3),
            List.of(4, 5, 6),
            List.of(7, 8, 9)
        );
        
        List<Integer> flattenedList = listOfLists.stream().flatMap(List::stream).collect(Collectors.toList());
        out.println("Flattened List: " + flattenedList);
    }

//        12. Group employees by department using collect(groupingBy())

    public static void employeesByDepartment() {
        Map<String, List<Employee>> employeesByDep = EmployeeData.getEmployees().stream().collect(Collectors.groupingBy(Employee::getDepartment));
        employeesByDep.forEach((s, employees) -> {
                    out.println(s);
                    employees.forEach(out::println);
                }
        );
    }

    //    13. Find the average salary of employees
//    13. Find the average salary of employees
    public static void averageSalary() {
        double avgSalary = EmployeeData.getEmployees().stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
        out.println("Average Salary : " + avgSalary);
    }
//   14. Get the top 3 highest paid employees
    public static void getTop3HighestSalaried() {

        EmployeeData.getEmployees().stream().sorted((a,b) -> (int) (b.getSalary()-a.getSalary())).limit(3).collect(Collectors.toList()).forEach(out::println);
    }
//        15. Convert a list to a Map where key=name, value=age

    public static void listToMap(){
        EmployeeData.getEmployees().stream().collect(Collectors.toMap(employee -> employee.getName(),employee -> employee.getAge())).forEach((k,v )-> {
                    out.println("key : " + k);
                    out.println("Value : "+ v);
                }
        );
    }


}
