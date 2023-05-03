package com.example.applicationevent.event;

import com.example.applicationevent.entity.User;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class UserRegisteredEvent extends ApplicationEvent {

    private final User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
