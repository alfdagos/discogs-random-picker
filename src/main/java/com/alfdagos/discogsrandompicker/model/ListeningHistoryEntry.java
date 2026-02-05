package com.alfdagos.discogsrandompicker.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an entry in the listening history.
 */
public class ListeningHistoryEntry {
    
    private final int discogsId;
    private final String artist;
    private final String title;
    private final String year;
    private final LocalDateTime listenedDate;
    private String notes;
    private Integer rating;
    
    public ListeningHistoryEntry(int discogsId, String artist, String title, String year, 
                                  LocalDateTime listenedDate) {
        this.discogsId = discogsId;
        this.artist = artist;
        this.title = title;
        this.year = year;
        this.listenedDate = listenedDate;
    }
    
    public static ListeningHistoryEntry fromAlbum(Album album) {
        return new ListeningHistoryEntry(
            album.getDiscogsId(),
            album.getArtistsAsString(),
            album.getTitle(),
            album.getYear(),
            LocalDateTime.now()
        );
    }
    
    public int getDiscogsId() {
        return discogsId;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getYear() {
        return year;
    }
    
    public LocalDateTime getListenedDate() {
        return listenedDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListeningHistoryEntry that = (ListeningHistoryEntry) o;
        return discogsId == that.discogsId && 
               Objects.equals(listenedDate, that.listenedDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(discogsId, listenedDate);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s) listened on %s", 
            artist, title, year, listenedDate);
    }
}
