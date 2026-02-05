package com.alfdagos.discogsrandompicker.exception;

/**
 * Exception thrown when there's an error with the application configuration.
 */
public class ConfigurationException extends Exception {
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
