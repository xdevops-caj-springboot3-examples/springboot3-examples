package com.example.openapispec.issue.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateIssueRequestDTO {
    @Schema(description = "The title of the issue.", example = "Found a bug")
    @NotBlank
    private String title;
    @Schema(description = "The contents of the issue.", example = "I'm having a problem with this.")
    @NotBlank
    private String body;
    @Schema(description = "Login for the user that this issue should be assigned to.", example = "William")
    @NotNull
    private String assignee;
}
