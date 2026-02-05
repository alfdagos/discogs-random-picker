package com.alfdagos.discogsrandompicker.service;

import com.alfdagos.discogsrandompicker.exception.HistoryException;
import com.alfdagos.discogsrandompicker.model.ListeningHistoryEntry;
import com.alfdagos.discogsrandompicker.model.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating statistics from listening history.
 */
public class StatisticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    
    private final HistoryService historyService;
    
    public StatisticsService(HistoryService historyService) {
        this.historyService = historyService;
    }
    
    /**
     * Generates comprehensive statistics from listening history.
     */
    public Statistics generateStatistics() throws HistoryException {
        logger.info("Generating statistics");
        
        List<ListeningHistoryEntry> history = historyService.loadHistory();
        Statistics stats = new Statistics();
        
        stats.setTotalAlbums(history.size());
        
        if (history.isEmpty()) {
            return stats;
        }
        
        // Count by year
        Map<String, Integer> byYear = new HashMap<>();
        for (ListeningHistoryEntry entry : history) {
            String year = entry.getYear();
            byYear.put(year, byYear.getOrDefault(year, 0) + 1);
        }
        stats.setAlbumsByYear(byYear);
        
        // Find most popular year
        String mostPopularYear = byYear.entrySet().stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        stats.setMostPopularYear(mostPopularYear);
        
        // Count by artist
        Map<String, Integer> byArtist = new HashMap<>();
        for (ListeningHistoryEntry entry : history) {
            String artist = entry.getArtist();
            byArtist.put(artist, byArtist.getOrDefault(artist, 0) + 1);
        }
        stats.setAlbumsByArtist(byArtist);
        
        // Find most listened artist
        String mostListenedArtist = byArtist.entrySet().stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        stats.setMostListenedArtist(mostListenedArtist);
        
        // Count by decade
        Map<String, Integer> byDecade = new HashMap<>();
        for (ListeningHistoryEntry entry : history) {
            try {
                int year = Integer.parseInt(entry.getYear());
                int decade = (year / 10) * 10;
                String decadeStr = decade + "s";
                byDecade.put(decadeStr, byDecade.getOrDefault(decadeStr, 0) + 1);
            } catch (NumberFormatException e) {
                // Skip invalid years
            }
        }
        stats.setAlbumsByDecade(byDecade);
        
        // Find most popular decade
        String mostPopularDecade = byDecade.entrySet().stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        stats.setMostPopularDecade(mostPopularDecade);
        
        logger.info("Statistics generated: {} total albums", stats.getTotalAlbums());
        return stats;
    }
    
    /**
     * Formats statistics as a readable string.
     */
    public String formatStatistics(Statistics stats) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║     LISTENING HISTORY STATISTICS     ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");
        
        sb.append(String.format("📀 Total Albums Listened: %d\n\n", stats.getTotalAlbums()));
        
        if (stats.getTotalAlbums() == 0) {
            sb.append("No listening history yet. Start listening!\n");
            return sb.toString();
        }
        
        sb.append(String.format("🎤 Most Listened Artist: %s (%d albums)\n", 
            stats.getMostListenedArtist(), 
            stats.getAlbumsByArtist().get(stats.getMostListenedArtist())));
        
        sb.append(String.format("📅 Most Popular Year: %s (%d albums)\n", 
            stats.getMostPopularYear(),
            stats.getAlbumsByYear().get(stats.getMostPopularYear())));
        
        sb.append(String.format("🕰️  Most Popular Decade: %s (%d albums)\n\n", 
            stats.getMostPopularDecade(),
            stats.getAlbumsByDecade().get(stats.getMostPopularDecade())));
        
        // Top 5 artists
        sb.append("🎸 Top 5 Artists:\n");
        stats.getAlbumsByArtist().entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> sb.append(String.format("   %s: %d albums\n", 
                entry.getKey(), entry.getValue())));
        
        sb.append("\n");
        
        // Top 5 years
        sb.append("📆 Top 5 Years:\n");
        stats.getAlbumsByYear().entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> sb.append(String.format("   %s: %d albums\n", 
                entry.getKey(), entry.getValue())));
        
        return sb.toString();
    }
}
