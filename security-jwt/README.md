# User register and authentication and secure your API with JWT token

## References

- [Spring boot 3.0 - Secure your API with JWT Token](https://www.youtube.com/watch?v=BVdQ3iuovg0)
- https://github.com/ali-bouali/spring-boot-3-jwt-security/blob/main/pom.xml


## Dependencies

- Spring Web for REST API
- Spring Data JPA for database access
- H2 database for database
- Spring Security for authentication and authorization
- Spring Validation for validation
- jjwt for JWT token handling
- Lombok for code generation


## Spring Security Components

Spring Security is a powerful framework that provides a wide range of components to secure your Spring Boot application. Some of the key components of Spring Security include:

1. Security Filter Chain: The security filter chain is the main component of Spring Security. It is responsible for authenticating and authorizing requests to your application. The security filter chain includes several filters that work together to secure your application.

2. Authentication: Authentication is the process of verifying the identity of a user. Spring Security provides several authentication mechanisms, including form-based authentication, basic authentication, OAuth2, and more.

3. Authorization: Authorization is the process of determining whether a user is allowed to access a specific resource or perform a specific action. Spring Security provides several authorization mechanisms, including role-based authorization, permission-based authorization, and more.

4. User Details Service: The user details service is responsible for loading user details, such as the user's username, password, and authorities. Spring Security provides several user details service implementations, including in-memory, JDBC-based, and LDAP-based implementations.

5. Password Encoding: Password encoding is the process of encrypting user passwords before storing them in a database. Spring Security provides several password encoding mechanisms, including BCrypt, PBKDF2, and more.

6. CSRF Protection: Cross-Site Request Forgery (CSRF) is a security vulnerability that allows attackers to execute unauthorized actions on behalf of authenticated users. Spring Security provides CSRF protection by default to prevent such attacks.

7. Session Management: Session management is the process of managing user sessions in a web application. Spring Security provides several session management features, including session fixation protection, concurrent session control, and more.


## Spring Security for database authentication

- UserDetails: A core interface in Spring Security that represents a user's security context. 
  It contains information about a user such as the user's username, password, authorities (roles), and whether the user's account is active or not.
- UserDetailsService: An interface that loads user-specific data. 
  It is used by the DaoAuthenticationProvider to load details about the user during authentication.
- PasswordEncoder: An interface for encoding passwords. 
  The BCryptPasswordEncoder class implements the PasswordEncoder interface and uses the BCrypt strong hashing function.
- AuthenticationManager: An interface that defines a contract for authentication providers.
- AuthenticationProvider: An interface that defines a contract for authentication providers that verify credentials and load user information.
- SecurityFilterChain: A filter chain that is responsible for authenticating and authorizing requests to your application.
- JWTService: A service which use `jjwt` library to handle JWT token generation and validation.



## Scenario

Scenarios:
- User register
- User authenticate (login)
- Access a secured API with JWT token

## User register

User register:
- Add the user into the database
- Generate a JWT token


### User Entity

The `UserDetails` interface is a core interface in Spring Security that represents a user's security context. 

It contains information about a user such as the user's username, password, authorities (roles), and whether the user's account is active or not.

Spring Security also has a `org.springframework.security.core.userdetails.User` class implements the `UserDetails` interface.

Create a `User` entity class implements `UserDetails` interface, and implements the methods.

The `User` class user `email` user as the username for authentication.

```java
    @Override
    public String getUsername() {
        return email;
    }
```

To simplify the demo, assume the user only has one role.

```java
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
```

### User password encryption
Save encrypted password instead of plain-text password into the database.

Define the bean.

See `config/ApplicationConfig.java`.

```java
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```

Save the encrypted password into the database.

See `register()` method in `auth/AuthenticationService.java`.

```java
        var user = User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();

        userRepository.save(user);
```

### Generate a JWT token

JWT stands for JSON Web Token. It is a compact, URL-safe means of representing claims to be transferred between two parties.
JWTs are commonly used for authentication and authorization in web applications.

A JWT token consists of three parts: a header, a payload, and a signature. The header contains information about the type of token and the signing algorithm used. The payload contains the claims, which are statements about an entity (typically, the user) and additional data.

The signature is used to verify that the message hasn't been tampered with.

References:
- https://jwt.io/

Generate a JWT token.

See `generateToken()` method in `config/JWTService.java`.

```java
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 24 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
```

Use OpenSSL generate a 256-bit security key with hex format.

```bash
openssl rand -hex 32
```

Set the security key (singing key) in `application.yaml`:

```yaml
jwt:
  signing:
    key: "da070a09e6c2b87c3a96b82e3460fed6e8212b9e6982e00f00fc5ad637efa75c"
```

The header is:

```json
{
  "alg": "HS256"
}
```

Notes:
- The signing algorithm is `HS256`, which means HMAC-SHA256.

The payload example is;

```json
{
  "sub": "john@example.com",
  "iat": 1683627324,
  "exp": 1683628764
}
```

Notes:
- `sub` is the subject, which is the username of the user.
- `iat` is the issued at time, which is the current time.
- `exp` is the expiration time, which is 24 hours later in this example.


## User authentication

### User authentication with database

Create `authenticationManager` bean.

```java
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
```

Create `authenticationProvider` bean.
```java
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
```

Notes:
- `DaoAuthenticationManager`: An authentication manager that authenticates against a user details service.

See `authenticationManager()` method in `config/ApplicationConfig.java`.

Set the authentication provider in security configuration.

Authenticate the user by username and password with AuthenticationManager.

```java
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // validate username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // generate a JWT token
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
```

In Spring Security, the AuthenticationManager interface is responsible for performing authentication operations. 

It is used by the AuthenticationProvider to authenticate a user's credentials and determine whether to grant access to a protected resource.

### Security configuration

```java
    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers("/api/v1/auth/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
        }
```

Notes:
- Disable CSRF protection.
- Allow all requests to `/api/v1/auth/**` without authentication.
- Authenticate all other requests.
- Set the session management to stateless, which means the server will not create a session for the user.
- Set the authentication provider. The authentication provider is used to authenticate the user by username and password.
- Add the JWT authentication filter before the username password authentication filter.


## Validate the JWT token

Validate the JWT token:
- Extract the JWT token from the authorization header, the token format is `Bearer <token>` in `Authorization` request header.
- Extract the username from the JWT token, i,e. `sub` claim in the JWT token play load.
- If the token is valid, it means:
  - The token is JWT format
  - The token is signed by the server
  - The token is not expired
- Load the user from the database by the username
- Set the user into the security context

See `config/JwtAuthenticationFilter.java`.


### Load user from the database by the user name

Create `userDetailsService` bean and override the `loadUserByUsername()` method.

See `userDetailsService()` method in `config/ApplicationConfig.java`.

```java
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
```
  



## Test


### Access demo without authentication
Request:

```bash
http :8080/api/v1/demo 
```

Response status code is `403`

### Register a user

Request:

```bash
http post :8080/api/v1/auth/register \
  firstName=john \
  lastName=date \
  email="john@example.com" \
  password="12345678Abcd"
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjgzNjI3MzI0LCJleHAiOjE2ODM2Mjg3NjR9.sKXBbkDQtVGMa_qKLwP-kHU-GgzAvuHSd0ITbi9oodw"
}
```

Decode the JWT token on https://jwt.io/ , the payload is like:

```json
{
  "sub": "john@example.com",
  "iat": 1683627324,
  "exp": 1683628764
}
```

Register another user:

```bash
http post :8080/api/v1/auth/register \
  firstName=tommy \
  lastName=gao \
  email="tommy@example.com" \
  password="password123"
```

### Authenticate with the user

Request:

```bash
http post :8080/api/v1/auth/authenticate \
  email="john@example.com" \
  password="12345678Abcd"
```

Response:

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjgzNjI4MDI5LCJleHAiOjE2ODM2Mjk0Njl9.fNDI_5af64Nglkh-2lSfg21s-M4iWzCT1NZws1pZF9c"
}
```

If the username or password is incorrect, the response as below:

```json
{
  "code": 401,
  "errors": [
    "Bad credentials"
  ],
  "message": "Bad credentials",
  "status": "UNAUTHORIZED"
}
```

### Access demo with the token

Request:

```bash
export JWT_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjgzNjI3MzI0LCJleHAiOjE2ODM2Mjg3NjR9.sKXBbkDQtVGMa_qKLwP-kHU-GgzAvuHSd0ITbi9oodw"

http :8080/api/v1/demo \
  --auth-type=bearer \
  --auth="${JWT_TOKEN}"
```

Response:

```bash
Hello from secured endpoint.
```

If the token is invalid, the response status code is `403`.



