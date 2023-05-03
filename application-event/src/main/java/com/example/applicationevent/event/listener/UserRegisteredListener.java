package com.example.applicationevent.event.listener;

import com.example.applicationevent.event.UserRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent) {
        System.out.println("Sending registration email: email address = " + userRegisteredEvent.getUser().getEmail());
    }
}
