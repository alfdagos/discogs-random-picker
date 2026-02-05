package com.alfdagos.discogsrandompicker.service;

import com.alfdagos.discogsrandompicker.exception.HistoryException;
import com.alfdagos.discogsrandompicker.model.ListeningHistoryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting listening history to various formats.
 */
public class ExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private static final DateTimeFormatter DISPLAY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private final HistoryService historyService;
    
    public ExportService(HistoryService historyService) {
        this.historyService = historyService;
    }
    
    /**
     * Exports listening history to CSV format.
     */
    public void exportToCsv(String filename) throws HistoryException {
        logger.info("Exporting history to CSV: {}", filename);
        
        List<ListeningHistoryEntry> history = historyService.loadHistory();
        
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("Discogs ID,Artist,Title,Year,Listened Date,Rating,Notes\n");
            
            // Write entries
            for (ListeningHistoryEntry entry : history) {
                writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%s,\"%s\"\n",
                    entry.getDiscogsId(),
                    escapeCsv(entry.getArtist()),
                    escapeCsv(entry.getTitle()),
                    entry.getYear(),
                    entry.getListenedDate().format(DISPLAY_FORMATTER),
                    entry.getRating() != null ? entry.getRating() : "",
                    entry.getNotes() != null ? escapeCsv(entry.getNotes()) : ""));
            }
            
            logger.info("Exported {} entries to CSV", history.size());
            
        } catch (IOException e) {
            throw new HistoryException("Failed to export to CSV: " + filename, e);
        }
    }
    
    /**
     * Exports listening history to HTML format.
     */
    public void exportToHtml(String filename) throws HistoryException {
        logger.info("Exporting history to HTML: {}", filename);
        
        List<ListeningHistoryEntry> history = historyService.loadHistory();
        
        try (FileWriter writer = new FileWriter(filename)) {
            // Write HTML header
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n<head>\n");
            writer.write("<meta charset=\"UTF-8\">\n");
            writer.write("<title>Listening History</title>\n");
            writer.write("<style>\n");
            writer.write("body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }\n");
            writer.write("h1 { color: #333; }\n");
            writer.write("table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
            writer.write("th { background: #333; color: white; padding: 12px; text-align: left; }\n");
            writer.write("td { padding: 10px; border-bottom: 1px solid #ddd; }\n");
            writer.write("tr:hover { background: #f9f9f9; }\n");
            writer.write(".rating { color: #f39c12; }\n");
            writer.write("</style>\n");
            writer.write("</head>\n<body>\n");
            writer.write("<h1>🎵 Listening History</h1>\n");
            writer.write(String.format("<p>Total albums: <strong>%d</strong></p>\n", history.size()));
            writer.write("<table>\n");
            writer.write("<tr><th>Artist</th><th>Title</th><th>Year</th><th>Date</th><th>Rating</th></tr>\n");
            
            // Write entries
            for (ListeningHistoryEntry entry : history) {
                writer.write("<tr>");
                writer.write(String.format("<td>%s</td>", escapeHtml(entry.getArtist())));
                writer.write(String.format("<td>%s</td>", escapeHtml(entry.getTitle())));
                writer.write(String.format("<td>%s</td>", entry.getYear()));
                writer.write(String.format("<td>%s</td>", 
                    entry.getListenedDate().format(DISPLAY_FORMATTER)));
                writer.write(String.format("<td class=\"rating\">%s</td>", 
                    entry.getRating() != null ? "★".repeat(entry.getRating()) : ""));
                writer.write("</tr>\n");
            }
            
            writer.write("</table>\n");
            writer.write("</body>\n</html>");
            
            logger.info("Exported {} entries to HTML", history.size());
            
        } catch (IOException e) {
            throw new HistoryException("Failed to export to HTML: " + filename, e);
        }
    }
    
    /**
     * Exports listening history to Markdown format.
     */
    public void exportToMarkdown(String filename) throws HistoryException {
        logger.info("Exporting history to Markdown: {}", filename);
        
        List<ListeningHistoryEntry> history = historyService.loadHistory();
        
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# 🎵 Listening History\n\n");
            writer.write(String.format("Total albums: **%d**\n\n", history.size()));
            writer.write("| Artist | Title | Year | Date | Rating |\n");
            writer.write("|--------|-------|------|------|--------|\n");
            
            // Write entries
            for (ListeningHistoryEntry entry : history) {
                writer.write(String.format("| %s | %s | %s | %s | %s |\n",
                    escapeMarkdown(entry.getArtist()),
                    escapeMarkdown(entry.getTitle()),
                    entry.getYear(),
                    entry.getListenedDate().format(DISPLAY_FORMATTER),
                    entry.getRating() != null ? "★".repeat(entry.getRating()) : ""));
            }
            
            logger.info("Exported {} entries to Markdown", history.size());
            
        } catch (IOException e) {
            throw new HistoryException("Failed to export to Markdown: " + filename, e);
        }
    }
    
    private String escapeCsv(String text) {
        if (text == null) return "";
        return text.replace("\"", "\"\"");
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
    
    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|");
    }
}
