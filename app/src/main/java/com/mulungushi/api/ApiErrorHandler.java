package com.mulungushi.api;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

/**
 * Role 7 - Central error handler for all API calls.
 * Maps exceptions and HTTP status codes into human-readable messages
 * that the UI layer can display.
 */
public class ApiErrorHandler {

    // Error categories the UI should handle
    public enum ErrorType {
        NO_NETWORK,          // device is offline
        TIMEOUT,             // server didn't respond in time
        VALIDATION,          // 400 — bad input from user
        UNAUTHORIZED,        // 401 — session expired or invalid token
        FORBIDDEN,           // 403 — no permission (role guard)
        NOT_FOUND,           // 404 — record doesn't exist
        CONFLICT,            // 409 — version conflict
        SERVER_ERROR,        // 500+ — backend problem
        UNKNOWN              // anything else
    }

    public static class ApiError {
        public final ErrorType type;
        public final int httpCode;      // 0 if not HTTP-related
        public final String message;    // user-friendly

        public ApiError(ErrorType type, int httpCode, String message) {
            this.type = type;
            this.httpCode = httpCode;
            this.message = message;
        }

        /**
         * True when the user should be redirected to login.
         */
        public boolean isSessionExpired() {
            return type == ErrorType.UNAUTHORIZED;
        }

        /**
         * True when the user can simply retry (offline/timeout).
         */
        public boolean isRetryable() {
            return type == ErrorType.NO_NETWORK
                    || type == ErrorType.TIMEOUT
                    || type == ErrorType.SERVER_ERROR;
        }

        @Override
        public String toString() {
            return "ApiError{" + type + ", code=" + httpCode + ", msg='" + message + "'}";
        }
    }

    /**
     * Use this in the Retrofit onFailure() callback.
     */
    public static ApiError fromThrowable(Throwable t) {
        if (t instanceof UnknownHostException) {
            return new ApiError(ErrorType.NO_NETWORK, 0,
                    "No internet connection. Check your network and try again.");
        }
        if (t instanceof SocketTimeoutException) {
            return new ApiError(ErrorType.TIMEOUT, 0,
                    "The server took too long to respond. Please retry.");
        }
        if (t instanceof IOException) {
            return new ApiError(ErrorType.NO_NETWORK, 0,
                    "Connection lost. Check your network.");
        }
        return new ApiError(ErrorType.UNKNOWN, 0,
                t.getMessage() != null ? t.getMessage() : "Unexpected error.");
    }

    /**
     * Use this in the Retrofit onResponse() callback when !response.isSuccessful().
     */
    public static ApiError fromResponse(Response<?> response) {
        int code = response.code();
        String serverMsg = response.message();

        switch (code) {
            case 400:
                return new ApiError(ErrorType.VALIDATION, 400,
                        serverMsg.isEmpty() ? "Invalid input. Please check your data." : serverMsg);
            case 401:
                return new ApiError(ErrorType.UNAUTHORIZED, 401,
                        "Your session has expired. Please log in again.");
            case 403:
                return new ApiError(ErrorType.FORBIDDEN, 403,
                        "You don't have permission to do that.");
            case 404:
                return new ApiError(ErrorType.NOT_FOUND, 404,
                        "The record was not found.");
            case 409:
                return new ApiError(ErrorType.CONFLICT, 409,
                        "This record was changed on the server. Please refresh and try again.");
            default:
                if (code >= 500) {
                    return new ApiError(ErrorType.SERVER_ERROR, code,
                            "The server had a problem. Try again in a moment.");
                }
                return new ApiError(ErrorType.UNKNOWN, code,
                        serverMsg.isEmpty() ? "Unexpected server response." : serverMsg);
        }
    }
}