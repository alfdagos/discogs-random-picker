package com.alfdagos.discogsrandompicker.config;

import com.alfdagos.discogsrandompicker.exception.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application configuration from properties file.
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    
    private final Properties properties;
    
    public ConfigManager() throws ConfigurationException {
        this(DEFAULT_CONFIG_FILE);
    }
    
    public ConfigManager(String configFile) throws ConfigurationException {
        this.properties = new Properties();
        loadConfiguration(configFile);
        validateConfiguration();
    }
    
    private void loadConfiguration(String configFile) throws ConfigurationException {
        logger.info("Loading configuration from {}", configFile);
        
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", configFile, e);
            throw new ConfigurationException(
                "Failed to load configuration file: " + configFile + 
                ". Please create it from config.properties.example", e);
        }
    }
    
    private void validateConfiguration() throws ConfigurationException {
        logger.debug("Validating configuration");
        
        String username = getDiscogsUsername();
        if (StringUtils.isBlank(username) || username.equals("your_discogs_username")) {
            throw new ConfigurationException(
                "Discogs username not configured. Please set 'discogs.username' in config.properties");
        }
        
        String token = getDiscogsToken();
        if (StringUtils.isBlank(token) || token.equals("your_discogs_api_token")) {
            throw new ConfigurationException(
                "Discogs API token not configured. Please set 'discogs.token' in config.properties. " +
                "Get your token from: https://www.discogs.com/settings/developers");
        }
        
        logger.debug("Configuration validated successfully");
    }
    
    public String getDiscogsUsername() {
        return properties.getProperty("discogs.username", "");
    }
    
    public String getDiscogsToken() {
        return properties.getProperty("discogs.token", "");
    }
    
    public String getSpotifyClientId() {
        return properties.getProperty("spotify.client.id", "");
    }
    
    public String getSpotifyClientSecret() {
        return properties.getProperty("spotify.client.secret", "");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
