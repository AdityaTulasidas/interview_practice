package com.interview.practice;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Component
class GreetingService {
    public String greet(String name) {
        return "Hello, " + name;
    }
}

@Service
public class SpringExample {
    @Autowired
    private GreetingService greetingService;

    public void showGreeting() {
        System.out.println(greetingService.greet("Spring"));
    }
}

