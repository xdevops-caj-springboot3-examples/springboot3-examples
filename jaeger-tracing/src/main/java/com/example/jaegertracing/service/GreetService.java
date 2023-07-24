package com.example.jaegertracing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GreetService {

    public String greet(String name) {
        log.info("Request received to say hello {}", name);
        return String.format("Hello %s", name);
    }
}
