package com.himaloyit.buildnation.ui.client.support;

/** Thrown when the Gateway (or the backend service behind it) returns an error {@code ApiResponse}. */
public class GatewayApiException extends RuntimeException {

    private final int status;

    public GatewayApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
