package com.alfdagos.discogsrandompicker.exception;

/**
 * Exception thrown when there's an error communicating with the Discogs API.
 */
public class DiscogsApiException extends Exception {
    
    private final int statusCode;
    
    public DiscogsApiException(String message) {
        super(message);
        this.statusCode = -1;
    }
    
    public DiscogsApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }
    
    public DiscogsApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}
