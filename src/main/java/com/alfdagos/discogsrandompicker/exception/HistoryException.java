package com.alfdagos.discogsrandompicker.exception;

/**
 * Exception thrown when there's an error managing the listening history.
 */
public class HistoryException extends Exception {
    
    public HistoryException(String message) {
        super(message);
    }
    
    public HistoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
