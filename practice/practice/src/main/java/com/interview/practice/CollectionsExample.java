package com.interview.practice;

import java.util.*;

public class CollectionsExample {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        Set<String> set = new HashSet<>(list);
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "One");
        map.put(2, "Two");
        System.out.println("List: " + list);
        System.out.println("Set: " + set);
        System.out.println("Map: " + map);
    }
}

