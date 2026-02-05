package com.alfdagos.discogsrandompicker.service;

import com.alfdagos.discogsrandompicker.exception.DiscogsApiException;
import com.alfdagos.discogsrandompicker.model.Album;
import com.alfdagos.discogsrandompicker.model.AlbumFilter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for interacting with the Discogs API.
 */
public class DiscogsService {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscogsService.class);
    private static final String API_BASE_URL = "https://api.discogs.com";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int MAX_FILTER_ATTEMPTS = 50;
    
    private final String username;
    private final String token;
    private final Gson gson;
    private final Random random;
    
    public DiscogsService(String username, String token) {
        this.username = username;
        this.token = token;
        this.gson = new Gson();
        this.random = new Random();
    }
    
    /**
     * Gets a random album from the user's collection.
     */
    public Album getRandomAlbum() throws DiscogsApiException {
        return getRandomAlbum(new AlbumFilter());
    }
    
    /**
     * Gets a random album from the user's collection matching the filter.
     */
    public Album getRandomAlbum(AlbumFilter filter) throws DiscogsApiException {
        logger.info("Fetching random album from collection for user: {}", username);
        
        int totalItems = getCollectionSize();
        logger.debug("Collection size: {} items", totalItems);
        
        if (totalItems == 0) {
            throw new DiscogsApiException("Collection is empty");
        }
        
        // If no filter, just get a random album
        if (filter.isEmpty()) {
            return fetchRandomAlbum(totalItems);
        }
        
        // With filter, we need to try multiple times
        for (int attempt = 0; attempt < MAX_FILTER_ATTEMPTS; attempt++) {
            Album album = fetchRandomAlbum(totalItems);
            if (filter.matches(album)) {
                logger.info("Found matching album after {} attempts", attempt + 1);
                return album;
            }
        }
        
        throw new DiscogsApiException(
            "Could not find album matching filter after " + MAX_FILTER_ATTEMPTS + " attempts. " +
            "Try relaxing your filter criteria.");
    }
    
    /**
     * Gets the total number of items in the collection.
     */
    public int getCollectionSize() throws DiscogsApiException {
        String url = String.format("%s/users/%s/collection/folders/0/releases?per_page=1", 
            API_BASE_URL, username);
        
        JsonObject response = makeRequest(url);
        
        if (response.has("pagination")) {
            JsonObject pagination = response.getAsJsonObject("pagination");
            return pagination.get("items").getAsInt();
        }
        
        throw new DiscogsApiException("Failed to get collection size");
    }
    
    /**
     * Gets all albums from the collection (paginated).
     */
    public List<Album> getAllAlbums() throws DiscogsApiException {
        return getAllAlbums(1, 100);
    }
    
    /**
     * Gets albums from the collection with pagination.
     */
    public List<Album> getAllAlbums(int page, int perPage) throws DiscogsApiException {
        logger.info("Fetching albums from collection (page {}, {} per page)", page, perPage);
        
        String url = String.format("%s/users/%s/collection/folders/0/releases?page=%d&per_page=%d",
            API_BASE_URL, username, page, perPage);
        
        JsonObject response = makeRequest(url);
        List<Album> albums = new ArrayList<>();
        
        if (response.has("releases")) {
            JsonArray releases = response.getAsJsonArray("releases");
            for (int i = 0; i < releases.size(); i++) {
                try {
                    Album album = Album.fromDiscogsJson(releases.get(i).getAsJsonObject());
                    albums.add(album);
                } catch (Exception e) {
                    logger.warn("Failed to parse album at index {}", i, e);
                }
            }
        }
        
        logger.info("Fetched {} albums", albums.size());
        return albums;
    }
    
    private Album fetchRandomAlbum(int totalItems) throws DiscogsApiException {
        int randomPage = random.nextInt(totalItems) + 1;
        
        String url = String.format("%s/users/%s/collection/folders/0/releases?page=%d&per_page=1",
            API_BASE_URL, username, randomPage);
        
        JsonObject response = makeRequest(url);
        
        if (response.has("releases")) {
            JsonArray releases = response.getAsJsonArray("releases");
            if (releases.size() > 0) {
                Album album = Album.fromDiscogsJson(releases.get(0).getAsJsonObject());
                logger.info("Fetched random album: {}", album);
                return album;
            }
        }
        
        throw new DiscogsApiException("Failed to fetch random album");
    }
    
    private JsonObject makeRequest(String urlString) throws DiscogsApiException {
        return makeRequest(urlString, 0);
    }
    
    private JsonObject makeRequest(String urlString, int retryCount) throws DiscogsApiException {
        try {
            logger.debug("Making request to: {}", urlString);
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Discogs token=" + token);
            conn.setRequestProperty("User-Agent", "DiscogsRandomPicker/2.0");
            
            int responseCode = conn.getResponseCode();
            logger.debug("Response code: {}", responseCode);
            
            // Handle rate limiting
            if (responseCode == 429) {
                if (retryCount < MAX_RETRIES) {
                    logger.warn("Rate limited, retrying in {}ms (attempt {}/{})", 
                        RETRY_DELAY_MS, retryCount + 1, MAX_RETRIES);
                    Thread.sleep(RETRY_DELAY_MS);
                    return makeRequest(urlString, retryCount + 1);
                } else {
                    throw new DiscogsApiException("Rate limited after " + MAX_RETRIES + " retries", 429);
                }
            }
            
            if (responseCode == 401) {
                throw new DiscogsApiException("Authentication failed. Check your Discogs token.", 401);
            }
            
            if (responseCode == 404) {
                throw new DiscogsApiException("Resource not found. Check your Discogs username.", 404);
            }
            
            if (responseCode != 200) {
                throw new DiscogsApiException("API request failed with status code: " + responseCode, 
                    responseCode);
            }
            
            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return gson.fromJson(response.toString(), JsonObject.class);
            
        } catch (IOException e) {
            throw new DiscogsApiException("Network error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DiscogsApiException("Request interrupted", e);
        }
    }
}
