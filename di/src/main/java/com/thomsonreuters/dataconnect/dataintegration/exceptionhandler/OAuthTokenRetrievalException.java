package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;

/**
 * Thrown when retrieval of an OAuth token fails (HTTP error, transport issue, empty body, etc).
 * Message is intentionally concise; full details should be available in logs.
 */
public class OAuthTokenRetrievalException extends RuntimeException {

    private final int statusCode;
    private final String responseSnippet;

    /**
     * Constructor for non-HTTP transport/unexpected errors.
     */
    public OAuthTokenRetrievalException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseSnippet = null;
    }

    /**
     * Constructor for HTTP status based failures.
     */
    public OAuthTokenRetrievalException(String message, int statusCode, String responseSnippet, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseSnippet = responseSnippet;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns a truncated (safe) snippet of the response body if available.
     */
    public String getResponseSnippet() {
        return responseSnippet;
    }
}
