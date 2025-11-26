package com.aditya.examples;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BasicStreamExmples {

//    1. Create a list of integers 1-10 and filter even numbers

    public static List<Integer> getEvenNumbers(){
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        numbers = numbers.stream().filter( num -> num %2 == 0).collect(Collectors.toList());
        System.out.println("numbers : "+ numbers);
        return numbers;
    }
// 2. Convert a list of strings to uppercase using map()
    public static List<String> ToUpperCase(){
        List<String> StringList = Arrays.asList("Aditya","Divya","Shubha");
        StringList = StringList.stream()
                .map(str -> str.toUpperCase()).collect(Collectors.toList());
        System.out.println("Uppercase : "+ StringList);
        return StringList;
    }
// 3. Find the sum of all numbers in a list using reduce()

    public static void sumOfAllNums() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("sum : "+ numbers.stream().reduce(0, (a,b) -> a+b));

    }
//    4. Count how many strings start with "A" in a list

    public static void stringStartsWithA(){
        List<String> stringList = Arrays.asList("Aditya","Divya","Shubha");
        stringList = stringList.stream().filter(str -> str.startsWith("A") || str.startsWith("a")).collect(Collectors.toList());
        System.out.println("String that starts with A or a : " +stringList);
    }
//    5. Get the first 5 elements from a stream of 1-100
    public static void getFirstFiveElements(){
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        numbers = numbers.stream().limit(5).collect(Collectors.toList());
        System.out.println("First five elements : " +numbers);
    }
}
