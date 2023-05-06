package com.example.openapispec.issue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateIssueResponseDTO {
    private String id;
    private String url;
    private String number;
    private String state;
    private String title;
    private String body;
    private String assignee;
    private String createdAt;
}
