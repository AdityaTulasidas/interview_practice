package com.interview.practice;

// Generics Example
class Box<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

public class GenericsExample {
    public static void main(String[] args) {
        Box<Integer> intBox = new Box<>();
        intBox.set(123);
        System.out.println("Box contains: " + intBox.get());
    }
}

