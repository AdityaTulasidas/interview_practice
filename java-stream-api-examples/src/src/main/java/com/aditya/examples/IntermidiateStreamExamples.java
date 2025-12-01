package com.aditya.examples;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.out;

/*

    ### Intermediate Level
    6. Remove duplicates from a list of integers
    7. Sort a list of strings by length (shortest first)
    8. Skip the first 3 elements and take the next 5
    9. Find the maximum value in a list of integers
    10. Check if all numbers in a list are positive
 */
public class IntermidiateStreamExamples {

    static List<Integer> intList = Arrays.asList(1, -2, -3, 4, 5, 6, 7, 8, 8, 6, 9, 20, 12, 13, 1, 4, 15, 16, 17, 18, 19, 20);
    static List<String> strList = Arrays.asList("Aditya", "Shubha", "Divya");

    //    6. Remove duplicates from a list of integers
    public static void removeDuplicates() {
        intList = intList.stream().distinct().collect(Collectors.toList());
        out.println("Distinct elements : " + intList);
    }

    //     7. Sort a list of strings by length (shortest first)
    public static void sortAsPerLength() {
        //can also be done this way
        List<String> sortedString1 = strList.stream().sorted(Comparator.comparing(String::length)).collect(Collectors.toList());
        List<String> sortedString = strList.stream().sorted((o1, o2) -> o1.length() - o2.length()).collect(Collectors.toList());
        out.println("SortedString :" + sortedString);
        out.println("SortedString :" + sortedString1);
    }
//         8. Skip the first 3 elements and take the next 5

    public static void skipFirts3Elements() {
        List<Integer> listAfter3 = intList.stream().skip(3).limit(5).collect(Collectors.toList());
        intList.stream().skip(3).limit(5).collect(Collectors.toList()).forEach(out::println);


        System.out.println("List After 3 elements : " + listAfter3);
    }

    //      9. Find the maximum value in a list of integers
    public static void maxOfList() {
        Optional<Integer> listAfter3 = intList.stream().max((a, b) -> a - b);

        System.out.println("List After 3 elements : " + listAfter3.get());
    }
//        10. Check if all numbers in a list are positive
    public static void filterPositiveNumbers(){
        // With negative numbers
        List<Integer> mixed = Arrays.asList(1, -2, 3, 4, 5);
        boolean allPositive = mixed.stream().allMatch(n -> n > 0);
        System.out.println(allPositive); // Output: false
    }



}
