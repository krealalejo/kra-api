package com.kra.api.infrastructure.github;

public class GitHubApiException extends RuntimeException {

    private final int httpStatus;

    public GitHubApiException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
