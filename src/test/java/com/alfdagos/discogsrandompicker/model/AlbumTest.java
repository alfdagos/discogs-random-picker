package com.alfdagos.discogsrandompicker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class AlbumTest {
    
    @Test
    void testAlbumBuilder() {
        Album album = new Album.Builder()
            .withDiscogsId(12345)
            .withTitle("Test Album")
            .withYear("1980")
            .build();
        
        assertEquals(12345, album.getDiscogsId());
        assertEquals("Test Album", album.getTitle());
        assertEquals("1980", album.getYear());
    }
    
    @Test
    void testFromDiscogsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 12345);
        
        JsonObject basicInfo = new JsonObject();
        basicInfo.addProperty("title", "Test Album");
        basicInfo.addProperty("year", 1980);
        
        JsonArray artists = new JsonArray();
        JsonObject artist = new JsonObject();
        artist.addProperty("name", "Test Artist");
        artists.add(artist);
        basicInfo.add("artists", artists);
        
        json.add("basic_information", basicInfo);
        
        Album album = Album.fromDiscogsJson(json);
        
        assertEquals(12345, album.getDiscogsId());
        assertEquals("Test Album", album.getTitle());
        assertEquals("1980", album.getYear());
        assertEquals("Test Artist", album.getArtistsAsString());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Album album1 = new Album.Builder().withDiscogsId(123).build();
        Album album2 = new Album.Builder().withDiscogsId(123).build();
        Album album3 = new Album.Builder().withDiscogsId(456).build();
        
        assertEquals(album1, album2);
        assertNotEquals(album1, album3);
        assertEquals(album1.hashCode(), album2.hashCode());
    }
    
    @Test
    void testToString() {
        Album album = new Album.Builder()
            .withTitle("Test Album")
            .withYear("1980")
            .build();
        
        String str = album.toString();
        assertTrue(str.contains("Test Album"));
        assertTrue(str.contains("1980"));
    }
}
