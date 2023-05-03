# Application Event

## Key concepts of Application Event

- Source: The source is the object that triggers the event.
- Event: An event is a custom class that represents a significant occurrence.
- Event Publisher: The event publisher is responsible for publishing the event.
- Event Listener: The event listener is responsible for receiving and handling the event.

## Define an application event

```java
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
```

Create an event class extends from `ApplicationEvent`.

## Publish an application event

```java
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
```

Create an event and then publish it by `ApplicationEventPublisher`.

## Listen to an application event

```java
@Component
public class UserRegisteredListener {

    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent) {
        System.out.println("Sending registration email: email address = " + userRegisteredEvent.getUser().getEmail());
    }
}
```

Use `@EventListhener` annotation to create a event listener method.

The event listener method receives the event and print the email address.

## Testing

Request:
```bash
http :8080/register firstName=Bruce lastName=Lee email=bruce@example.com password=12345678 matchingPassword=12345678
```

Response status code is `200`

Check logs:
```text
Sending registration email: email address = bruce@example.com
```

## Appendix

### If Application Event can be used cross microservices

By default, Spring Boot ApplicationEvent is designed to be used within the same application context. However, Spring provides several mechanisms to enable events to be sent across different contexts or even across different processes.

One approach is to use a Spring Integration or Spring Cloud Stream to handle the messaging layer for events. 

Another approach is to use a messaging system such as RabbitMQ, Apache Kafka, or ActiveMQ to handle the messaging layer for events.


## Troubleshooting

## H2 database "Syntax error in SQL statement ... expected identifier"

The word `User` is H2 reserved keyword.

We need change the table name of `User` entity class as `t_user`.

```java
@Table(name="t_user")
```

Or let H2 don't use `User` as a keyword.

References:
- https://blog.dkwr.de/development/fix-h2-error-expected-identifeir/