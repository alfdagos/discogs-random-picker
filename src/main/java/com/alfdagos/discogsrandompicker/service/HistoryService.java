package com.alfdagos.discogsrandompicker.service;

import com.alfdagos.discogsrandompicker.exception.HistoryException;
import com.alfdagos.discogsrandompicker.model.Album;
import com.alfdagos.discogsrandompicker.model.ListeningHistoryEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing listening history.
 */
public class HistoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
    private static final String DEFAULT_HISTORY_FILE = "listening_history.json";
    
    private final String historyFile;
    private final Gson gson;
    
    public HistoryService() {
        this(DEFAULT_HISTORY_FILE);
    }
    
    public HistoryService(String historyFile) {
        this.historyFile = historyFile;
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    }
    
    /**
     * Adds an album to the listening history.
     */
    public void addToHistory(Album album) throws HistoryException {
        logger.info("Adding album to history: {}", album);
        
        ListeningHistoryEntry entry = ListeningHistoryEntry.fromAlbum(album);
        List<ListeningHistoryEntry> history = loadHistory();
        history.add(entry);
        saveHistory(history);
        
        logger.info("Album added to history successfully");
    }
    
    /**
     * Adds a listening history entry.
     */
    public void addToHistory(ListeningHistoryEntry entry) throws HistoryException {
        logger.info("Adding entry to history: {}", entry);
        
        List<ListeningHistoryEntry> history = loadHistory();
        history.add(entry);
        saveHistory(history);
        
        logger.info("Entry added to history successfully");
    }
    
    /**
     * Checks if an album is already in the history.
     */
    public boolean isInHistory(Album album) throws HistoryException {
        List<ListeningHistoryEntry> history = loadHistory();
        return history.stream()
            .anyMatch(entry -> entry.getDiscogsId() == album.getDiscogsId());
    }
    
    /**
     * Gets the number of times an album was listened.
     */
    public int getListenCount(int discogsId) throws HistoryException {
        List<ListeningHistoryEntry> history = loadHistory();
        return (int) history.stream()
            .filter(entry -> entry.getDiscogsId() == discogsId)
            .count();
    }
    
    /**
     * Loads all listening history entries.
     */
    public List<ListeningHistoryEntry> loadHistory() throws HistoryException {
        File file = new File(historyFile);
        
        if (!file.exists()) {
            logger.debug("History file does not exist, returning empty list");
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            
            if (jsonArray == null || jsonArray.size() == 0) {
                return new ArrayList<>();
            }
            
            List<ListeningHistoryEntry> history = new ArrayList<>();
            
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    JsonObject obj = jsonArray.get(i).getAsJsonObject();
                    
                    int discogsId = obj.has("discogs_id") ? obj.get("discogs_id").getAsInt() : 0;
                    String artist = obj.has("artist") ? obj.get("artist").getAsString() : "Unknown";
                    String title = obj.has("title") ? obj.get("title").getAsString() : "Unknown";
                    String year = obj.has("year") ? obj.get("year").getAsString() : "Unknown";
                    
                    LocalDateTime listenedDate = LocalDateTime.now();
                    if (obj.has("listened_date")) {
                        String dateStr = obj.get("listened_date").getAsString();
                        listenedDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    
                    ListeningHistoryEntry entry = new ListeningHistoryEntry(
                        discogsId, artist, title, year, listenedDate);
                    
                    if (obj.has("notes")) {
                        entry.setNotes(obj.get("notes").getAsString());
                    }
                    
                    if (obj.has("rating")) {
                        entry.setRating(obj.get("rating").getAsInt());
                    }
                    
                    history.add(entry);
                } catch (Exception e) {
                    logger.warn("Failed to parse history entry at index {}", i, e);
                }
            }
            
            logger.debug("Loaded {} history entries", history.size());
            return history;
            
        } catch (IOException e) {
            throw new HistoryException("Failed to load history file: " + historyFile, e);
        }
    }
    
    /**
     * Gets recent listening history entries.
     */
    public List<ListeningHistoryEntry> getRecentHistory(int limit) throws HistoryException {
        List<ListeningHistoryEntry> history = loadHistory();
        
        return history.stream()
            .sorted(Comparator.comparing(ListeningHistoryEntry::getListenedDate).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Clears all listening history.
     */
    public void clearHistory() throws HistoryException {
        logger.warn("Clearing all listening history");
        saveHistory(new ArrayList<>());
        logger.info("History cleared successfully");
    }
    
    private void saveHistory(List<ListeningHistoryEntry> history) throws HistoryException {
        try (FileWriter writer = new FileWriter(historyFile)) {
            JsonArray jsonArray = new JsonArray();
            
            for (ListeningHistoryEntry entry : history) {
                JsonObject obj = new JsonObject();
                obj.addProperty("discogs_id", entry.getDiscogsId());
                obj.addProperty("artist", entry.getArtist());
                obj.addProperty("title", entry.getTitle());
                obj.addProperty("year", entry.getYear());
                obj.addProperty("listened_date", 
                    entry.getListenedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                if (entry.getNotes() != null) {
                    obj.addProperty("notes", entry.getNotes());
                }
                
                if (entry.getRating() != null) {
                    obj.addProperty("rating", entry.getRating());
                }
                
                jsonArray.add(obj);
            }
            
            gson.toJson(jsonArray, writer);
            logger.debug("Saved {} history entries", history.size());
            
        } catch (IOException e) {
            throw new HistoryException("Failed to save history file: " + historyFile, e);
        }
    }
    
    /**
     * Custom adapter for LocalDateTime serialization/deserialization.
     */
    private static class LocalDateTimeAdapter extends com.google.gson.TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }
        
        @Override
        public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }
}
