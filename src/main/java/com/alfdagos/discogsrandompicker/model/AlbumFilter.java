package com.alfdagos.discogsrandompicker.model;

/**
 * Filter criteria for selecting albums from the collection.
 */
public class AlbumFilter {
    
    private String genre;
    private String year;
    private String format;
    private String artist;
    private Integer minYear;
    private Integer maxYear;
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public Integer getMinYear() {
        return minYear;
    }
    
    public void setMinYear(Integer minYear) {
        this.minYear = minYear;
    }
    
    public Integer getMaxYear() {
        return maxYear;
    }
    
    public void setMaxYear(Integer maxYear) {
        this.maxYear = maxYear;
    }
    
    public boolean matches(Album album) {
        if (genre != null && !album.getGenres().stream()
                .anyMatch(g -> g.toLowerCase().contains(genre.toLowerCase()))) {
            return false;
        }
        
        if (year != null && !album.getYear().equals(year)) {
            return false;
        }
        
        if (format != null && !album.getFormats().stream()
                .anyMatch(f -> f.toLowerCase().contains(format.toLowerCase()))) {
            return false;
        }
        
        if (artist != null && !album.getArtistsAsString().toLowerCase()
                .contains(artist.toLowerCase())) {
            return false;
        }
        
        if (minYear != null || maxYear != null) {
            try {
                int albumYear = Integer.parseInt(album.getYear());
                if (minYear != null && albumYear < minYear) {
                    return false;
                }
                if (maxYear != null && albumYear > maxYear) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isEmpty() {
        return genre == null && year == null && format == null && 
               artist == null && minYear == null && maxYear == null;
    }
}
