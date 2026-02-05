package com.alfdagos.discogsrandompicker.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistics about the listening history.
 */
public class Statistics {
    
    private int totalAlbums;
    private Map<String, Integer> albumsByYear;
    private Map<String, Integer> albumsByArtist;
    private Map<String, Integer> albumsByDecade;
    private String mostListenedArtist;
    private String mostPopularDecade;
    private String mostPopularYear;
    
    public Statistics() {
        this.albumsByYear = new HashMap<>();
        this.albumsByArtist = new HashMap<>();
        this.albumsByDecade = new HashMap<>();
    }
    
    public int getTotalAlbums() {
        return totalAlbums;
    }
    
    public void setTotalAlbums(int totalAlbums) {
        this.totalAlbums = totalAlbums;
    }
    
    public Map<String, Integer> getAlbumsByYear() {
        return new HashMap<>(albumsByYear);
    }
    
    public void setAlbumsByYear(Map<String, Integer> albumsByYear) {
        this.albumsByYear = new HashMap<>(albumsByYear);
    }
    
    public Map<String, Integer> getAlbumsByArtist() {
        return new HashMap<>(albumsByArtist);
    }
    
    public void setAlbumsByArtist(Map<String, Integer> albumsByArtist) {
        this.albumsByArtist = new HashMap<>(albumsByArtist);
    }
    
    public Map<String, Integer> getAlbumsByDecade() {
        return new HashMap<>(albumsByDecade);
    }
    
    public void setAlbumsByDecade(Map<String, Integer> albumsByDecade) {
        this.albumsByDecade = new HashMap<>(albumsByDecade);
    }
    
    public String getMostListenedArtist() {
        return mostListenedArtist;
    }
    
    public void setMostListenedArtist(String mostListenedArtist) {
        this.mostListenedArtist = mostListenedArtist;
    }
    
    public String getMostPopularDecade() {
        return mostPopularDecade;
    }
    
    public void setMostPopularDecade(String mostPopularDecade) {
        this.mostPopularDecade = mostPopularDecade;
    }
    
    public String getMostPopularYear() {
        return mostPopularYear;
    }
    
    public void setMostPopularYear(String mostPopularYear) {
        this.mostPopularYear = mostPopularYear;
    }
}
