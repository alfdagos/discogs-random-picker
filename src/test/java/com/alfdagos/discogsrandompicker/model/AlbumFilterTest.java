package com.alfdagos.discogsrandompicker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumFilterTest {
    
    @Test
    void testEmptyFilter() {
        AlbumFilter filter = new AlbumFilter();
        assertTrue(filter.isEmpty());
    }
    
    @Test
    void testFilterNotEmpty() {
        AlbumFilter filter = new AlbumFilter();
        filter.setGenre("Rock");
        assertFalse(filter.isEmpty());
    }
    
    @Test
    void testGenreMatching() {
        Album album = new Album.Builder()
            .withGenres(java.util.Arrays.asList("Rock", "Pop"))
            .build();
        
        AlbumFilter filter = new AlbumFilter();
        filter.setGenre("Rock");
        
        assertTrue(filter.matches(album));
    }
    
    @Test
    void testYearMatching() {
        Album album = new Album.Builder()
            .withYear("1980")
            .build();
        
        AlbumFilter filter = new AlbumFilter();
        filter.setYear("1980");
        
        assertTrue(filter.matches(album));
    }
    
    @Test
    void testYearRangeMatching() {
        Album album = new Album.Builder()
            .withYear("1985")
            .build();
        
        AlbumFilter filter = new AlbumFilter();
        filter.setMinYear(1980);
        filter.setMaxYear(1990);
        
        assertTrue(filter.matches(album));
    }
    
    @Test
    void testYearRangeNotMatching() {
        Album album = new Album.Builder()
            .withYear("1995")
            .build();
        
        AlbumFilter filter = new AlbumFilter();
        filter.setMinYear(1980);
        filter.setMaxYear(1990);
        
        assertFalse(filter.matches(album));
    }
}
