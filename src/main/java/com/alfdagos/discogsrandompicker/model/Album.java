package com.alfdagos.discogsrandompicker.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an album from the Discogs collection.
 */
public class Album {
    
    private final int discogsId;
    private final String title;
    private final List<String> artists;
    private final String year;
    private final List<String> formats;
    private final List<String> genres;
    private final List<String> styles;
    private final String coverImage;
    
    private Album(Builder builder) {
        this.discogsId = builder.discogsId;
        this.title = builder.title;
        this.artists = builder.artists;
        this.year = builder.year;
        this.formats = builder.formats;
        this.genres = builder.genres;
        this.styles = builder.styles;
        this.coverImage = builder.coverImage;
    }
    
    /**
     * Creates an Album from a Discogs API JSON response.
     */
    public static Album fromDiscogsJson(JsonObject json) {
        JsonObject basicInfo = json.has("basic_information") 
            ? json.getAsJsonObject("basic_information") 
            : json;
        
        Builder builder = new Builder()
            .withDiscogsId(json.has("id") ? json.get("id").getAsInt() : 0)
            .withTitle(basicInfo.has("title") ? basicInfo.get("title").getAsString() : "Unknown");
        
        // Parse artists
        if (basicInfo.has("artists")) {
            JsonArray artistsArray = basicInfo.getAsJsonArray("artists");
            List<String> artistsList = new ArrayList<>();
            for (int i = 0; i < artistsArray.size(); i++) {
                JsonObject artist = artistsArray.get(i).getAsJsonObject();
                if (artist.has("name")) {
                    artistsList.add(artist.get("name").getAsString());
                }
            }
            builder.withArtists(artistsList);
        }
        
        // Parse year
        if (basicInfo.has("year")) {
            builder.withYear(String.valueOf(basicInfo.get("year").getAsInt()));
        }
        
        // Parse formats
        if (basicInfo.has("formats")) {
            JsonArray formatsArray = basicInfo.getAsJsonArray("formats");
            List<String> formatsList = new ArrayList<>();
            for (int i = 0; i < formatsArray.size(); i++) {
                JsonObject format = formatsArray.get(i).getAsJsonObject();
                if (format.has("name")) {
                    formatsList.add(format.get("name").getAsString());
                }
            }
            builder.withFormats(formatsList);
        }
        
        // Parse genres
        if (basicInfo.has("genres")) {
            JsonArray genresArray = basicInfo.getAsJsonArray("genres");
            List<String> genresList = new ArrayList<>();
            for (int i = 0; i < genresArray.size(); i++) {
                genresList.add(genresArray.get(i).getAsString());
            }
            builder.withGenres(genresList);
        }
        
        // Parse styles
        if (basicInfo.has("styles")) {
            JsonArray stylesArray = basicInfo.getAsJsonArray("styles");
            List<String> stylesList = new ArrayList<>();
            for (int i = 0; i < stylesArray.size(); i++) {
                stylesList.add(stylesArray.get(i).getAsString());
            }
            builder.withStyles(stylesList);
        }
        
        // Parse cover image
        if (basicInfo.has("cover_image")) {
            builder.withCoverImage(basicInfo.get("cover_image").getAsString());
        } else if (basicInfo.has("thumb")) {
            builder.withCoverImage(basicInfo.get("thumb").getAsString());
        }
        
        return builder.build();
    }
    
    public int getDiscogsId() {
        return discogsId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public List<String> getArtists() {
        return new ArrayList<>(artists);
    }
    
    public String getArtistsAsString() {
        return String.join(", ", artists);
    }
    
    public String getYear() {
        return year;
    }
    
    public List<String> getFormats() {
        return new ArrayList<>(formats);
    }
    
    public String getFormatsAsString() {
        return String.join(", ", formats);
    }
    
    public List<String> getGenres() {
        return new ArrayList<>(genres);
    }
    
    public String getGenresAsString() {
        return String.join(", ", genres);
    }
    
    public List<String> getStyles() {
        return new ArrayList<>(styles);
    }
    
    public String getStylesAsString() {
        return String.join(", ", styles);
    }
    
    public String getCoverImage() {
        return coverImage;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return discogsId == album.discogsId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(discogsId);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", getArtistsAsString(), title, year);
    }
    
    /**
     * Builder for Album instances.
     */
    public static class Builder {
        private int discogsId;
        private String title = "Unknown";
        private List<String> artists = new ArrayList<>();
        private String year = "Unknown";
        private List<String> formats = new ArrayList<>();
        private List<String> genres = new ArrayList<>();
        private List<String> styles = new ArrayList<>();
        private String coverImage = "";
        
        public Builder withDiscogsId(int discogsId) {
            this.discogsId = discogsId;
            return this;
        }
        
        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder withArtists(List<String> artists) {
            this.artists = new ArrayList<>(artists);
            return this;
        }
        
        public Builder withYear(String year) {
            this.year = year;
            return this;
        }
        
        public Builder withFormats(List<String> formats) {
            this.formats = new ArrayList<>(formats);
            return this;
        }
        
        public Builder withGenres(List<String> genres) {
            this.genres = new ArrayList<>(genres);
            return this;
        }
        
        public Builder withStyles(List<String> styles) {
            this.styles = new ArrayList<>(styles);
            return this;
        }
        
        public Builder withCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }
        
        public Album build() {
            return new Album(this);
        }
    }
}
