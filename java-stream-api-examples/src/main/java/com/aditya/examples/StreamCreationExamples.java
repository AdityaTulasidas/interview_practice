package com.aditya.examples;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Examples of different ways to create streams in Java
 */
public class StreamCreationExamples {

    public static void demonstrateAll() {
        System.out.println("\n=== STREAM CREATION EXAMPLES ===\n");

        fromCollection();
        fromArray();
        fromValues();
        usingStreamBuilder();
        usingStreamGenerate();
        usingStreamIterate();
        primitiveStreams();
        infiniteStreams();
        emptyStream();
        streamOfNullable();
    }

    // 1. Stream from Collection
    private static void fromCollection() {
        System.out.println("1. Stream from Collection:");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        names.stream()
             .forEach(name -> System.out.print(name + " "));
        System.out.println("\n");
    }

    // 2. Stream from Array
    private static void fromArray() {
        System.out.println("2. Stream from Array:");
        String[] arr = {"Java", "Python", "JavaScript", "C++"};
        
        // Method 1: Arrays.stream()
        Arrays.stream(arr).forEach(lang -> System.out.print(lang + " "));
        System.out.println();
        
        // Method 2: Stream.of()
        Stream.of(arr).forEach(lang -> System.out.print(lang + " "));
        System.out.println("\n");
    }

    // 3. Stream from Values
    private static void fromValues() {
        System.out.println("3. Stream from Individual Values:");
        Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        numbers.forEach(num -> System.out.print(num + " "));
        System.out.println("\n");
    }

    // 4. Using Stream.Builder
    private static void usingStreamBuilder() {
        System.out.println("4. Using Stream.Builder:");
        Stream.Builder<String> builder = Stream.builder();
        
        builder.add("Spring")
               .add("Hibernate")
               .add("JPA")
               .add("MyBatis");
        
        builder.build().forEach(tech -> System.out.print(tech + " "));
        System.out.println("\n");
    }

    // 5. Using Stream.generate()
    private static void usingStreamGenerate() {
        System.out.println("5. Using Stream.generate() - Generate 5 random numbers:");
        Stream.generate(Math::random)
              .limit(5)
              .forEach(num -> System.out.printf("%.3f ", num));
        System.out.println("\n");
    }

    // 6. Using Stream.iterate()
    private static void usingStreamIterate() {
        System.out.println("6. Using Stream.iterate():");
        
        // Generate even numbers from 0 to 20
        Stream.iterate(0, n -> n + 2)
              .limit(11)
              .forEach(num -> System.out.print(num + " "));
        System.out.println();
        
        // Fibonacci sequence (first 10 numbers)
        Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
              .limit(10)
              .map(f -> f[0])
              .forEach(num -> System.out.print(num + " "));
        System.out.println("\n");
    }

    // 7. Primitive Streams
    private static void primitiveStreams() {
        System.out.println("7. Primitive Streams:");
        
        // IntStream
        System.out.print("IntStream (1-10): ");
        IntStream.range(1, 11).forEach(i -> System.out.print(i + " "));
        System.out.println();
        
        // IntStream inclusive
        System.out.print("IntStream inclusive (1-10): ");
        IntStream.rangeClosed(1, 10).forEach(i -> System.out.print(i + " "));
        System.out.println();
        
        // DoubleStream
        System.out.print("DoubleStream: ");
        java.util.stream.DoubleStream.of(1.1, 2.2, 3.3, 4.4)
                                     .forEach(d -> System.out.printf("%.1f ", d));
        System.out.println();
        
        // LongStream
        System.out.print("LongStream: ");
        java.util.stream.LongStream.rangeClosed(1, 5)
                                   .forEach(l -> System.out.print(l + " "));
        System.out.println("\n");
    }

    // 8. Infinite Streams
    private static void infiniteStreams() {
        System.out.println("8. Infinite Streams (limited to first 10):");
        
        // Infinite stream of constants
        Stream.generate(() -> "Hello")
              .limit(5)
              .forEach(str -> System.out.print(str + " "));
        System.out.println();
        
        // Infinite stream with iterate
        Stream.iterate(1, n -> n * 2)
              .limit(10)
              .forEach(num -> System.out.print(num + " "));
        System.out.println("\n");
    }

    // 9. Empty Stream
    private static void emptyStream() {
        System.out.println("9. Empty Stream:");
        Stream<String> emptyStream = Stream.empty();
        System.out.println("Empty stream count: " + emptyStream.count());
        System.out.println();
    }

    // 10. Stream.ofNullable() - Java 9+
    private static void streamOfNullable() {
        System.out.println("10. Stream.ofNullable() - Java 9+:");
        
        String value = "NonNull";
        Stream.ofNullable(value).forEach(System.out::println);
        
        String nullValue = null;
        long count = Stream.ofNullable(nullValue).count();
        System.out.println("Count of null stream: " + count);
        System.out.println();
    }




}
