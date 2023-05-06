package com.example.openapispec.issue.controller;

import com.example.openapispec.issue.dto.response.CreateIssueResponseDTO;
import com.example.openapispec.issue.dto.request.CreateIssueRequestDTO;
import com.example.openapispec.issue.dto.response.QueryIssueResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Tag(name = "Issues", description = "Use the REST API to manage issues and pull requests.")
public class IssueController {

    /**
     * Example: https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#create-an-issue
     * @param owner
     * @param repo
     * @param createIssueRequestDTO
     * @return
     */
    @PostMapping("/repos/{owner}/{repo}/issues")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create an issue",
            description = "Any user with pull access to a repository can create an issue. " +
                    "If issues are disabled in the repository, the API returns a 410 Gone status.",
            responses = {
                    @ApiResponse (
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse (
                            description = "Forbidden",
                            responseCode = "403"
                    ),
                    @ApiResponse (
                            description = "Resource not found",
                            responseCode = "404"
                    ),
                    @ApiResponse (
                            description = "Gone",
                            responseCode = "410"
                    ),
                    @ApiResponse (
                            description = "Validation failed, or the endpoint has been spammed.",
                            responseCode = "422"
                    ),
                    @ApiResponse (
                            description = "Service unavailable",
                            responseCode = "503"
                    )
            }
    )
    public CreateIssueResponseDTO createIssue(@PathVariable("owner")
                                                  @Schema(description = "The account owner of the repository. The name is not case sensitive.")
                                                  String owner,
                                              @PathVariable("repo")
                                              @Schema(description = "The name of the repository. The name is not case sensitive.")
                                              String repo,
                                              @RequestBody CreateIssueRequestDTO createIssueRequestDTO) {

        String issueId = "1";
        String issueNumber = "123";
        String issueUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/issues/" + issueNumber;

        return CreateIssueResponseDTO.builder()
                .id(issueId)
                .title(createIssueRequestDTO.getTitle())
                .body(createIssueRequestDTO.getBody())
                .state("open")
                .number(issueNumber)
                .url(issueUrl)
                .assignee(createIssueRequestDTO.getAssignee())
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")))
                .build();
    }

    /**
     * Example: https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#list-repository-issues
     * @param owner
     * @param repo
     * @return
     */
    @GetMapping("/repos/{owner}/{repo}/issues")
    @Operation(
            summary = "List repository issues",
            description = "List issues in a repository. Only open issues will be listed.",
            responses = {
                    @ApiResponse (
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse (
                            description = "Moved permanently",
                            responseCode = "301"
                    ),
                    @ApiResponse (
                            description = "Resource not found",
                            responseCode = "404"
                    ),
                    @ApiResponse (
                            description = "Validation failed, or the endpoint has been spammed.",
                            responseCode = "422"
                    )
            }
    )
    public List<QueryIssueResponseDTO> findIssues(@PathVariable("owner")
                                                      @Schema(description = "The account owner of the repository. The name is not case sensitive.")
                                                      String owner,
                                                  @PathVariable("repo")
                                                  @Schema(description = "The name of the repository. The name is not case sensitive.")
                                                  String repo) {
        String issueNumber = "123";
        String issueUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/issues/" + issueNumber;

        QueryIssueResponseDTO queryIssueResponseDTO = QueryIssueResponseDTO.builder()
                .id("1")
                .title("Found a bug")
                .body("I have a problem with this.")
                .state("open")
                .number(issueNumber)
                .url(issueUrl)
                .assignee("Tommy")
                .createdAt("2023-05-06T10:12:07Z")
                .build();
        return List.of(queryIssueResponseDTO);
    }
}
