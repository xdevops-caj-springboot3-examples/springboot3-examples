package com.example.applicationevent.controller;

import com.example.applicationevent.dto.UserRegisterDTO;
import com.example.applicationevent.entity.User;
import com.example.applicationevent.event.UserRegisteredEvent;
import com.example.applicationevent.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher publisher;

    public RegisterController(UserRepository userRepository, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        User user = User.builder()
                .firstName(userRegisterDTO.getFirstName())
                .lastName(userRegisterDTO.getLastName())
                .email(userRegisterDTO.getEmail())
                .password(userRegisterDTO.getPassword())
                .role("USER")
                .build();

        userRepository.save(user);

        // trigger and publish event
        publisher.publishEvent(new UserRegisteredEvent(this, user));
    }
}
