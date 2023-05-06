package com.example.openapispec.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(
        info = @Info(
                title = "Issues",
                version = "2022-11-28",
                description = "Use the REST API to view and manage issues, including issue assignees, comments, labels, and milestones.",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                termsOfService = "https://docs.github.com/en/site-policy/github-terms/github-terms-of-service",
                contact = @Contact(
                        name = "GitHub",
                        url = "https://github.com",
                        email = "api@github.com"
                )
        ),
        servers = {
                @Server(
                        description = "GitHub",
                        url = "https://api.github.com"
                ),
                @Server(
                        description = "Local Env",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenAPIConfig {
}
