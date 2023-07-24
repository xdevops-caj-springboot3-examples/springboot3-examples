package com.example.jaegertracing.controller;

import com.example.jaegertracing.service.GreetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GreetController {

    private final GreetService greetService;

    public GreetController(GreetService greetService) {
        this.greetService = greetService;
    }

    @GetMapping("/greet/{name}")
    public String greet(@PathVariable("name") String name) {
        log.info("Request received to greet {}", name);
        return greetService.greet(name);
    }
}
