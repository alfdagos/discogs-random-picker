package com.alfdagos.discogsrandompicker.service;

import com.alfdagos.discogsrandompicker.exception.HistoryException;
import com.alfdagos.discogsrandompicker.model.Album;
import com.alfdagos.discogsrandompicker.model.ListeningHistoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryServiceTest {
    
    @TempDir
    Path tempDir;
    
    private HistoryService historyService;
    
    @BeforeEach
    void setUp() {
        File historyFile = tempDir.resolve("test_history.json").toFile();
        historyService = new HistoryService(historyFile.getAbsolutePath());
    }
    
    @Test
    void testAddToHistory() throws HistoryException {
        Album album = new Album.Builder()
            .withDiscogsId(123)
            .withTitle("Test Album")
            .withYear("1980")
            .build();
        
        historyService.addToHistory(album);
        
        List<ListeningHistoryEntry> history = historyService.loadHistory();
        assertEquals(1, history.size());
        assertEquals(123, history.get(0).getDiscogsId());
    }
    
    @Test
    void testIsInHistory() throws HistoryException {
        Album album = new Album.Builder()
            .withDiscogsId(123)
            .withTitle("Test Album")
            .build();
        
        assertFalse(historyService.isInHistory(album));
        
        historyService.addToHistory(album);
        
        assertTrue(historyService.isInHistory(album));
    }
    
    @Test
    void testGetListenCount() throws HistoryException {
        Album album = new Album.Builder()
            .withDiscogsId(123)
            .withTitle("Test Album")
            .build();
        
        assertEquals(0, historyService.getListenCount(123));
        
        historyService.addToHistory(album);
        assertEquals(1, historyService.getListenCount(123));
        
        historyService.addToHistory(album);
        assertEquals(2, historyService.getListenCount(123));
    }
    
    @Test
    void testClearHistory() throws HistoryException {
        Album album = new Album.Builder()
            .withDiscogsId(123)
            .withTitle("Test Album")
            .build();
        
        historyService.addToHistory(album);
        assertEquals(1, historyService.loadHistory().size());
        
        historyService.clearHistory();
        assertEquals(0, historyService.loadHistory().size());
    }
}
