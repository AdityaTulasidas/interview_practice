package com.interview.practice;

// Multithreading Example
public class ThreadExample {
    public static void main(String[] args) {
        Runnable task = () -> System.out.println("Thread running: " + Thread.currentThread().getName());
        Thread thread = new Thread(task);
        thread.start();
    }
}

