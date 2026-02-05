package com.alfdagos.discogsrandompicker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Discogs Random Picker - A Java CLI application to pick random albums from Discogs collection
 * with Spotify integration for listening history tracking.
 */
public class DiscogsRandomPicker {

    private static final String CONFIG_FILE = "config.properties";
    private static final String HISTORY_FILE = "listening_history.json";
    private static Properties config;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            System.out.println("=== Discogs Random Album Picker ===\n");
            
            // Load configuration
            config = loadConfig();
            
            // Get random album from Discogs collection
            JsonObject album = getRandomAlbum();
            
            if (album != null) {
                displayAlbumInfo(album);
                
                // Ask if user wants to mark as listened
                if (askToMarkAsListened()) {
                    saveToHistory(album);
                    System.out.println("\n✓ Album saved to listening history!");
                }
            } else {
                System.out.println("Failed to fetch album from Discogs.");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        }
        return props;
    }

    private static JsonObject getRandomAlbum() throws IOException {
        String username = config.getProperty("discogs.username");
        String token = config.getProperty("discogs.token");
        
        // First, get the collection count
        String collectionUrl = String.format(
            "https://api.discogs.com/users/%s/collection/folders/0/releases?per_page=1",
            username
        );
        
        JsonObject collectionResponse = makeDiscogsRequest(collectionUrl, token);
        if (collectionResponse == null || !collectionResponse.has("pagination")) {
            return null;
        }
        
        int totalItems = collectionResponse.getAsJsonObject("pagination").get("items").getAsInt();
        
        // Pick a random page and item
        Random random = new Random();
        int randomPage = random.nextInt(Math.max(1, totalItems)) + 1;
        
        // Fetch that specific release
        String randomUrl = String.format(
            "https://api.discogs.com/users/%s/collection/folders/0/releases?per_page=1&page=%d",
            username, randomPage
        );
        
        JsonObject randomResponse = makeDiscogsRequest(randomUrl, token);
        if (randomResponse != null && randomResponse.has("releases")) {
            JsonArray releases = randomResponse.getAsJsonArray("releases");
            if (releases.size() > 0) {
                return releases.get(0).getAsJsonObject();
            }
        }
        
        return null;
    }

    private static JsonObject makeDiscogsRequest(String urlString, String token) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Discogs token=" + token);
        conn.setRequestProperty("User-Agent", "DiscogsRandomPicker/1.0");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return gson.fromJson(response.toString(), JsonObject.class);
        }
        
        return null;
    }

    private static void displayAlbumInfo(JsonObject album) {
        JsonObject basicInfo = album.getAsJsonObject("basic_information");
        
        String title = basicInfo.has("title") ? basicInfo.get("title").getAsString() : "Unknown";
        String artist = "Unknown Artist";
        
        if (basicInfo.has("artists")) {
            JsonArray artists = basicInfo.getAsJsonArray("artists");
            if (artists.size() > 0) {
                artist = artists.get(0).getAsJsonObject().get("name").getAsString();
            }
        }
        
        String year = basicInfo.has("year") ? basicInfo.get("year").getAsString() : "Unknown";
        String format = "Unknown";
        
        if (basicInfo.has("formats")) {
            JsonArray formats = basicInfo.getAsJsonArray("formats");
            if (formats.size() > 0) {
                JsonObject formatObj = formats.get(0).getAsJsonObject();
                format = formatObj.has("name") ? formatObj.get("name").getAsString() : "Unknown";
            }
        }
        
        System.out.println("Album Selected:");
        System.out.println("───────────────────────────────────");
        System.out.println("Artist: " + artist);
        System.out.println("Title:  " + title);
        System.out.println("Year:   " + year);
        System.out.println("Format: " + format);
        System.out.println("───────────────────────────────────");
    }

    private static boolean askToMarkAsListened() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nMark as listened? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    private static void saveToHistory(JsonObject album) throws IOException {
        List<JsonObject> history = loadHistory();
        
        JsonObject basicInfo = album.getAsJsonObject("basic_information");
        JsonObject historyEntry = new JsonObject();
        
        historyEntry.addProperty("title", basicInfo.has("title") ? basicInfo.get("title").getAsString() : "Unknown");
        
        if (basicInfo.has("artists")) {
            JsonArray artists = basicInfo.getAsJsonArray("artists");
            if (artists.size() > 0) {
                historyEntry.addProperty("artist", artists.get(0).getAsJsonObject().get("name").getAsString());
            }
        }
        
        historyEntry.addProperty("year", basicInfo.has("year") ? basicInfo.get("year").getAsString() : "Unknown");
        historyEntry.addProperty("listened_date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        if (album.has("id")) {
            historyEntry.addProperty("discogs_id", album.get("id").getAsInt());
        }
        
        history.add(historyEntry);
        
        // Save to file
        try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
            gson.toJson(history, writer);
        }
    }

    private static List<JsonObject> loadHistory() throws IOException {
        File historyFile = new File(HISTORY_FILE);
        if (!historyFile.exists()) {
            return new ArrayList<>();
        }
        
        String content = new String(Files.readAllBytes(Paths.get(HISTORY_FILE)));
        if (content.trim().isEmpty() || content.trim().equals("[]")) {
            return new ArrayList<>();
        }
        
        JsonArray jsonArray = gson.fromJson(content, JsonArray.class);
        List<JsonObject> history = new ArrayList<>();
        
        for (int i = 0; i < jsonArray.size(); i++) {
            history.add(jsonArray.get(i).getAsJsonObject());
        }
        
        return history;
    }
}
