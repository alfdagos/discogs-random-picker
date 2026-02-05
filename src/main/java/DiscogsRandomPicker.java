import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Discogs Random Album Picker
 * 
 * A CLI application that picks a random album from your Discogs collection,
 * searches for it on Spotify, and maintains a listening history.
 * 
 * Features:
 * - Fetches random albums from Discogs collection via API
 * - Searches for albums on Spotify via API
 * - Displays album information with emoji-enhanced output
 * - Maintains persistent listening history in JSON format
 * - Supports --history flag to view past picks
 * 
 * @author Discogs Random Picker Team
 * @version 1.0.0
 */
public class DiscogsRandomPicker {
    
    // Configuration constants
    private static final String CONFIG_FILE = "config.properties";
    private static final String HISTORY_FILE = "listening_history.json";
    private static final String USER_AGENT = "DiscogsRandomPicker/1.0";
    
    // API endpoints
    private static final String DISCOGS_API_BASE = "https://api.discogs.com";
    private static final String SPOTIFY_AUTH_URL = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search";
    
    // Configuration properties
    private Properties config;
    private Gson gson;
    
    /**
     * Main entry point of the application
     */
    public static void main(String[] args) {
        DiscogsRandomPicker app = new DiscogsRandomPicker();
        
        try {
            // Check for --history flag
            if (args.length > 0 && args[0].equals("--history")) {
                app.displayHistory();
            } else {
                app.run();
            }
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            if (Boolean.parseBoolean(System.getenv("DEBUG"))) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
    
    /**
     * Constructor - initializes Gson and loads configuration
     */
    public DiscogsRandomPicker() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();
        this.config = loadConfiguration();
    }
    
    /**
     * Main application flow
     */
    private void run() throws Exception {
        System.out.println("\n🎵 Discogs Random Album Picker 🎵\n");
        
        // Get Discogs configuration
        String discogsToken = getConfigValue("discogs.token");
        String discogsUsername = getConfigValue("discogs.username");
        
        // Get Spotify configuration
        String spotifyClientId = getConfigValue("spotify.client.id");
        String spotifyClientSecret = getConfigValue("spotify.client.secret");
        
        // Step 1: Get collection count
        System.out.println("📚 Fetching your Discogs collection...");
        int collectionCount = getCollectionCount(discogsUsername, discogsToken);
        System.out.println("   Found " + collectionCount + " albums in your collection!");
        
        // Step 2: Pick random album from collection
        System.out.println("\n🎲 Picking a random album...");
        JsonObject album = getRandomAlbum(discogsUsername, discogsToken, collectionCount);
        
        // Step 3: Extract album information
        AlbumInfo albumInfo = extractAlbumInfo(album);
        System.out.println("   Selected: " + albumInfo.artist + " - " + albumInfo.title);
        
        // Step 4: Search on Spotify
        System.out.println("\n🔍 Searching on Spotify...");
        String spotifyToken = getSpotifyAccessToken(spotifyClientId, spotifyClientSecret);
        JsonObject spotifyResult = searchSpotifyAlbum(albumInfo.artist, albumInfo.title, spotifyToken);
        
        if (spotifyResult != null) {
            albumInfo.spotifyUrl = spotifyResult.get("external_urls")
                .getAsJsonObject()
                .get("spotify")
                .getAsString();
            System.out.println("   ✅ Found on Spotify!");
        } else {
            System.out.println("   ⚠️  Album not found on Spotify");
        }
        
        // Step 5: Display album details
        displayAlbumDetails(albumInfo);
        
        // Step 6: Save to history
        saveToHistory(albumInfo);
        System.out.println("\n💾 Saved to listening history!");
        
        System.out.println("\n✨ Enjoy your music! ✨\n");
    }
    
    /**
     * Loads configuration from config.properties file or environment variables
     */
    private Properties loadConfiguration() {
        Properties props = new Properties();
        
        // Try to load from config file first
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
            } catch (IOException e) {
                System.err.println("⚠️  Warning: Could not load config file, falling back to environment variables");
            }
        }
        
        // Override with environment variables if present
        String[] envVars = {
            "DISCOGS_TOKEN", "DISCOGS_USERNAME",
            "SPOTIFY_CLIENT_ID", "SPOTIFY_CLIENT_SECRET"
        };
        
        String[] propKeys = {
            "discogs.token", "discogs.username",
            "spotify.client.id", "spotify.client.secret"
        };
        
        for (int i = 0; i < envVars.length; i++) {
            String envValue = System.getenv(envVars[i]);
            if (envValue != null && !envValue.isEmpty()) {
                props.setProperty(propKeys[i], envValue);
            }
        }
        
        return props;
    }
    
    /**
     * Gets a configuration value with validation
     */
    private String getConfigValue(String key) throws Exception {
        String value = config.getProperty(key);
        if (value == null || value.isEmpty() || value.startsWith("YOUR_")) {
            throw new Exception("Missing configuration: " + key + 
                ". Please set it in config.properties or as an environment variable.");
        }
        return value;
    }
    
    /**
     * Gets the total count of albums in the user's Discogs collection
     */
    private int getCollectionCount(String username, String token) throws Exception {
        String urlString = String.format("%s/users/%s/collection/folders/0/releases?per_page=1", 
            DISCOGS_API_BASE, username);
        
        JsonObject response = makeDiscogsApiCall(urlString, token);
        return response.getAsJsonObject("pagination").get("items").getAsInt();
    }
    
    /**
     * Gets a random album from the user's Discogs collection
     */
    private JsonObject getRandomAlbum(String username, String token, int collectionCount) throws Exception {
        // Pick a random page
        Random random = new Random();
        int randomPage = random.nextInt(collectionCount) + 1;
        
        String urlString = String.format("%s/users/%s/collection/folders/0/releases?page=%d&per_page=1", 
            DISCOGS_API_BASE, username, randomPage);
        
        JsonObject response = makeDiscogsApiCall(urlString, token);
        JsonArray releases = response.getAsJsonArray("releases");
        
        if (releases.size() == 0) {
            throw new Exception("No albums found in your collection");
        }
        
        return releases.get(0).getAsJsonObject();
    }
    
    /**
     * Makes a call to the Discogs API with retry logic
     */
    private JsonObject makeDiscogsApiCall(String urlString, String token) throws Exception {
        int maxRetries = Integer.parseInt(config.getProperty("api.max.retries", "3"));
        int retryDelay = Integer.parseInt(config.getProperty("api.retry.delay", "1000"));
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setRequestProperty("Authorization", "Discogs token=" + token);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == 200) {
                    String response = readResponse(conn.getInputStream());
                    return gson.fromJson(response, JsonObject.class);
                } else if (responseCode == 429) {
                    // Rate limited, wait and retry
                    if (attempt < maxRetries) {
                        Thread.sleep(retryDelay * attempt);
                        continue;
                    }
                    throw new Exception("Discogs API rate limit exceeded");
                } else {
                    String error = readResponse(conn.getErrorStream());
                    throw new Exception("Discogs API error (" + responseCode + "): " + error);
                }
            } catch (IOException e) {
                if (attempt < maxRetries) {
                    Thread.sleep(retryDelay);
                    continue;
                }
                throw new Exception("Failed to connect to Discogs API: " + e.getMessage());
            }
        }
        
        throw new Exception("Failed to fetch data from Discogs after " + maxRetries + " attempts");
    }
    
    /**
     * Gets a Spotify access token using Client Credentials flow
     */
    private String getSpotifyAccessToken(String clientId, String clientSecret) throws Exception {
        URL url = new URL(SPOTIFY_AUTH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        
        // Basic authentication
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        // Request body
        String body = "grant_type=client_credentials";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            String response = readResponse(conn.getInputStream());
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            return jsonResponse.get("access_token").getAsString();
        } else {
            String error = readResponse(conn.getErrorStream());
            throw new Exception("Spotify authentication failed (" + responseCode + "): " + error);
        }
    }
    
    /**
     * Searches for an album on Spotify
     */
    private JsonObject searchSpotifyAlbum(String artist, String title, String token) throws Exception {
        String query = artist + " " + title;
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String urlString = SPOTIFY_SEARCH_URL + "?q=" + encodedQuery + "&type=album&limit=1";
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            String response = readResponse(conn.getInputStream());
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray albums = jsonResponse.getAsJsonObject("albums").getAsJsonArray("items");
            
            if (albums.size() > 0) {
                return albums.get(0).getAsJsonObject();
            }
        }
        
        return null; // Album not found
    }
    
    /**
     * Extracts album information from Discogs API response
     */
    private AlbumInfo extractAlbumInfo(JsonObject album) {
        AlbumInfo info = new AlbumInfo();
        
        JsonObject basicInfo = album.getAsJsonObject("basic_information");
        
        info.title = basicInfo.get("title").getAsString();
        
        // Get artist name
        JsonArray artists = basicInfo.getAsJsonArray("artists");
        if (artists.size() > 0) {
            info.artist = artists.get(0).getAsJsonObject().get("name").getAsString();
        }
        
        // Get year
        if (basicInfo.has("year") && !basicInfo.get("year").isJsonNull()) {
            info.year = basicInfo.get("year").getAsInt();
        }
        
        // Get formats
        if (basicInfo.has("formats")) {
            JsonArray formats = basicInfo.getAsJsonArray("formats");
            if (formats.size() > 0) {
                JsonObject format = formats.get(0).getAsJsonObject();
                info.format = format.get("name").getAsString();
                
                // Get format details (e.g., "LP, Album")
                if (format.has("descriptions")) {
                    JsonArray descriptions = format.getAsJsonArray("descriptions");
                    List<String> descList = new ArrayList<>();
                    for (JsonElement desc : descriptions) {
                        descList.add(desc.getAsString());
                    }
                    info.formatDetails = String.join(", ", descList);
                }
            }
        }
        
        // Get genres
        if (basicInfo.has("genres")) {
            JsonArray genres = basicInfo.getAsJsonArray("genres");
            List<String> genreList = new ArrayList<>();
            for (JsonElement genre : genres) {
                genreList.add(genre.getAsString());
            }
            info.genres = String.join(", ", genreList);
        }
        
        // Get styles
        if (basicInfo.has("styles")) {
            JsonArray styles = basicInfo.getAsJsonArray("styles");
            List<String> styleList = new ArrayList<>();
            for (JsonElement style : styles) {
                styleList.add(style.getAsString());
            }
            info.styles = String.join(", ", styleList);
        }
        
        // Get Discogs URL
        if (basicInfo.has("resource_url")) {
            String resourceUrl = basicInfo.get("resource_url").getAsString();
            // Convert API URL to web URL
            info.discogsUrl = resourceUrl.replace("api.discogs.com", "www.discogs.com")
                                        .replace("/releases/", "/release/");
        }
        
        info.timestamp = new Date();
        
        return info;
    }
    
    /**
     * Displays album details with nice formatting and emojis
     */
    private void displayAlbumDetails(AlbumInfo album) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              🎼 YOUR RANDOM ALBUM PICK 🎼                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  🎤 Artist:  " + album.artist);
        System.out.println("  💿 Album:   " + album.title);
        
        if (album.year > 0) {
            System.out.println("  📅 Year:    " + album.year);
        }
        
        if (album.format != null) {
            String formatLine = "  📀 Format:  " + album.format;
            if (album.formatDetails != null) {
                formatLine += " (" + album.formatDetails + ")";
            }
            System.out.println(formatLine);
        }
        
        if (album.genres != null && !album.genres.isEmpty()) {
            System.out.println("  🎸 Genres:  " + album.genres);
        }
        
        if (album.styles != null && !album.styles.isEmpty()) {
            System.out.println("  🎹 Styles:  " + album.styles);
        }
        
        System.out.println();
        System.out.println("  🔗 Links:");
        
        if (album.discogsUrl != null) {
            System.out.println("     Discogs:  " + album.discogsUrl);
        }
        
        if (album.spotifyUrl != null) {
            System.out.println("     Spotify:  " + album.spotifyUrl);
        }
        
        System.out.println();
    }
    
    /**
     * Saves album information to listening history
     */
    private void saveToHistory(AlbumInfo album) throws IOException {
        List<AlbumInfo> history = loadHistory();
        history.add(0, album); // Add to beginning of list
        
        // Limit history to last 100 entries
        if (history.size() > 100) {
            history = history.subList(0, 100);
        }
        
        String json = gson.toJson(history);
        Files.write(Paths.get(HISTORY_FILE), json.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Loads listening history from JSON file
     */
    private List<AlbumInfo> loadHistory() {
        File historyFile = new File(HISTORY_FILE);
        
        if (!historyFile.exists()) {
            return new ArrayList<>();
        }
        
        try {
            String json = new String(Files.readAllBytes(Paths.get(HISTORY_FILE)), StandardCharsets.UTF_8);
            AlbumInfo[] albums = gson.fromJson(json, AlbumInfo[].class);
            return new ArrayList<>(Arrays.asList(albums));
        } catch (Exception e) {
            System.err.println("⚠️  Warning: Could not load history file, starting fresh");
            return new ArrayList<>();
        }
    }
    
    /**
     * Displays the listening history
     */
    private void displayHistory() {
        List<AlbumInfo> history = loadHistory();
        
        if (history.isEmpty()) {
            System.out.println("\n📭 Your listening history is empty. Pick an album first!\n");
            return;
        }
        
        System.out.println("\n📜 Listening History (" + history.size() + " albums)\n");
        System.out.println("════════════════════════════════════════════════════════════");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (int i = 0; i < history.size(); i++) {
            AlbumInfo album = history.get(i);
            System.out.println();
            System.out.println((i + 1) + ". " + album.artist + " - " + album.title);
            
            if (album.year > 0) {
                System.out.println("   Year: " + album.year);
            }
            
            if (album.timestamp != null) {
                System.out.println("   Picked: " + sdf.format(album.timestamp));
            }
            
            if (album.spotifyUrl != null) {
                System.out.println("   Spotify: " + album.spotifyUrl);
            }
        }
        
        System.out.println("\n════════════════════════════════════════════════════════════\n");
    }
    
    /**
     * Utility method to read response from input stream
     */
    private String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    
    /**
     * Inner class to hold album information
     */
    private static class AlbumInfo {
        String artist;
        String title;
        int year;
        String format;
        String formatDetails;
        String genres;
        String styles;
        String discogsUrl;
        String spotifyUrl;
        Date timestamp;
    }
}
