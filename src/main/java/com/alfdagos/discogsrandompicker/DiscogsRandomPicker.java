package com.alfdagos.discogsrandompicker;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alfdagos.discogsrandompicker.config.ConfigManager;
import com.alfdagos.discogsrandompicker.exception.ConfigurationException;
import com.alfdagos.discogsrandompicker.exception.DiscogsApiException;
import com.alfdagos.discogsrandompicker.exception.HistoryException;
import com.alfdagos.discogsrandompicker.model.Album;
import com.alfdagos.discogsrandompicker.model.AlbumFilter;
import com.alfdagos.discogsrandompicker.model.ListeningHistoryEntry;
import com.alfdagos.discogsrandompicker.model.Statistics;
import com.alfdagos.discogsrandompicker.service.DiscogsService;
import com.alfdagos.discogsrandompicker.service.ExportService;
import com.alfdagos.discogsrandompicker.service.HistoryService;
import com.alfdagos.discogsrandompicker.service.StatisticsService;

/**
 * Discogs Random Picker - Enhanced CLI application.
 * 
 * @version 2.0
 * @author alfdagos
 */
public class DiscogsRandomPicker {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscogsRandomPicker.class);
    private static final String VERSION = "2.0";
    
    private final ConfigManager config;
    private final DiscogsService discogsService;
    private final HistoryService historyService;
    private final StatisticsService statisticsService;
    private final ExportService exportService;
    
    public DiscogsRandomPicker() throws ConfigurationException {
        this.config = new ConfigManager();
        this.discogsService = new DiscogsService(
            config.getDiscogsUsername(), 
            config.getDiscogsToken());
        this.historyService = new HistoryService();
        this.statisticsService = new StatisticsService(historyService);
        this.exportService = new ExportService(historyService);
    }
    
    public static void main(String[] args) {
        // Check for help/version before initialization
        if (args.length > 0 && (args[0].equals("--help") || args[0].equals("-h") || 
                                args[0].equals("--version") || args[0].equals("-v"))) {
            Options options = buildOptions();
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(100);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║        DISCOGS RANDOM ALBUM PICKER v" + VERSION + "                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
            
            formatter.printHelp("java -jar discogs-random-picker.jar [OPTIONS]", 
                "\nOptions:", options, 
                "\nExamples:\n" +
                "  Pick random album:          java -jar discogs-random-picker.jar\n" +
                "  Filter by genre:            java -jar discogs-random-picker.jar --genre Rock\n" +
                "  Filter by year:             java -jar discogs-random-picker.jar --year 1980\n" +
                "  Filter by decade:           java -jar discogs-random-picker.jar --min-year 1970 --max-year 1979\n" +
                "  Show statistics:            java -jar discogs-random-picker.jar --stats\n" +
                "  Show history (last 10):     java -jar discogs-random-picker.jar --history --limit 10\n" +
                "  Export to CSV:              java -jar discogs-random-picker.jar --export csv\n" +
                "  Export to HTML:             java -jar discogs-random-picker.jar --export html -o myhistory.html\n");
            return;
        }
        
        try {
            DiscogsRandomPicker app = new DiscogsRandomPicker();
            
            if (args.length == 0) {
                // Default behavior: pick random album
                app.pickRandomAlbum(new AlbumFilter());
            } else {
                // Parse command line arguments
                app.parseAndExecute(args);
            }
            
        } catch (ConfigurationException e) {
            System.err.println("❌ Configuration Error: " + e.getMessage());
            logger.error("Configuration error", e);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            logger.error("Unexpected error", e);
            System.exit(1);
        }
    }
    
    private void parseAndExecute(String[] args) throws Exception {
        Options options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("help")) {
                printHelp(options);
                return;
            }
            
            if (cmd.hasOption("version")) {
                System.out.println("Discogs Random Picker v" + VERSION);
                return;
            }
            
            if (cmd.hasOption("stats")) {
                showStatistics();
                return;
            }
            
            if (cmd.hasOption("history")) {
                showHistory(cmd);
                return;
            }
            
            if (cmd.hasOption("export")) {
                exportHistory(cmd);
                return;
            }
            
            if (cmd.hasOption("collection-size")) {
                showCollectionSize();
                return;
            }
            
            // Default: pick random album with optional filters
            AlbumFilter filter = buildFilter(cmd);
            pickRandomAlbum(filter);
            
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            printHelp(options);
            System.exit(1);
        }
    }
    
    private static Options buildOptions() {
        Options options = new Options();
        
        options.addOption("h", "help", false, "Show help message");
        options.addOption("v", "version", false, "Show version");
        options.addOption("s", "stats", false, "Show listening statistics");
        options.addOption(null, "history", false, "Show listening history");
        options.addOption(null, "collection-size", false, "Show collection size");
        
        options.addOption(Option.builder()
            .longOpt("limit")
            .hasArg()
            .argName("NUMBER")
            .desc("Limit number of history entries to show")
            .build());
        
        options.addOption(Option.builder("g")
            .longOpt("genre")
            .hasArg()
            .argName("GENRE")
            .desc("Filter by genre")
            .build());
        
        options.addOption(Option.builder("y")
            .longOpt("year")
            .hasArg()
            .argName("YEAR")
            .desc("Filter by year")
            .build());
        
        options.addOption(Option.builder("f")
            .longOpt("format")
            .hasArg()
            .argName("FORMAT")
            .desc("Filter by format (e.g., Vinyl, CD)")
            .build());
        
        options.addOption(Option.builder("a")
            .longOpt("artist")
            .hasArg()
            .argName("ARTIST")
            .desc("Filter by artist name")
            .build());
        
        options.addOption(Option.builder()
            .longOpt("min-year")
            .hasArg()
            .argName("YEAR")
            .desc("Minimum year")
            .build());
        
        options.addOption(Option.builder()
            .longOpt("max-year")
            .hasArg()
            .argName("YEAR")
            .desc("Maximum year")
            .build());
        
        options.addOption(Option.builder("e")
            .longOpt("export")
            .hasArg()
            .argName("FORMAT")
            .desc("Export history (csv, html, markdown)")
            .build());
        
        options.addOption(Option.builder("o")
            .longOpt("output")
            .hasArg()
            .argName("FILE")
            .desc("Output filename for export")
            .build());
        
        options.addOption(null, "no-duplicate", false, "Skip albums already in history");
        
        return options;
    }
    
    private AlbumFilter buildFilter(CommandLine cmd) {
        AlbumFilter filter = new AlbumFilter();
        
        if (cmd.hasOption("genre")) {
            filter.setGenre(cmd.getOptionValue("genre"));
        }
        
        if (cmd.hasOption("year")) {
            filter.setYear(cmd.getOptionValue("year"));
        }
        
        if (cmd.hasOption("format")) {
            filter.setFormat(cmd.getOptionValue("format"));
        }
        
        if (cmd.hasOption("artist")) {
            filter.setArtist(cmd.getOptionValue("artist"));
        }
        
        if (cmd.hasOption("min-year")) {
            try {
                filter.setMinYear(Integer.parseInt(cmd.getOptionValue("min-year")));
            } catch (NumberFormatException e) {
                logger.warn("Invalid min-year value", e);
            }
        }
        
        if (cmd.hasOption("max-year")) {
            try {
                filter.setMaxYear(Integer.parseInt(cmd.getOptionValue("max-year")));
            } catch (NumberFormatException e) {
                logger.warn("Invalid max-year value", e);
            }
        }
        
        return filter;
    }
    
    private void pickRandomAlbum(AlbumFilter filter) throws DiscogsApiException, HistoryException {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   🎲 DISCOGS RANDOM ALBUM PICKER 🎲   ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        logger.info("Picking random album");
        Album album = discogsService.getRandomAlbum(filter);
        
        displayAlbumInfo(album);
        
        // Check if already in history
        if (historyService.isInHistory(album)) {
            int count = historyService.getListenCount(album.getDiscogsId());
            System.out.println("\n⚠️  You've already listened to this album " + count + " time(s)");
        }
        
        // Ask to mark as listened
        if (askToMarkAsListened()) {
            historyService.addToHistory(album);
            System.out.println("\n✅ Album added to listening history!");
        } else {
            System.out.println("\n⏭️  Skipped");
        }
    }
    
    private void displayAlbumInfo(Album album) {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│           ALBUM INFORMATION            │");
        System.out.println("└────────────────────────────────────────┘\n");
        
        System.out.println("🎤 Artist:  " + album.getArtistsAsString());
        System.out.println("💿 Title:   " + album.getTitle());
        System.out.println("📅 Year:    " + album.getYear());
        System.out.println("📀 Format:  " + album.getFormatsAsString());
        
        if (!album.getGenres().isEmpty()) {
            System.out.println("🎵 Genres:  " + album.getGenresAsString());
        }
        
        if (!album.getStyles().isEmpty()) {
            System.out.println("🎼 Styles:  " + album.getStylesAsString());
        }
        
        System.out.println("🔗 ID:      " + album.getDiscogsId());
        
        if (!album.getCoverImage().isEmpty() && !album.getCoverImage().equals("")) {
            System.out.println("🖼️  Cover:   " + album.getCoverImage());
        }
    }
    
    private boolean askToMarkAsListened() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n❓ Mark as listened? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
    
    private void showStatistics() throws HistoryException {
        logger.info("Showing statistics");
        Statistics stats = statisticsService.generateStatistics();
        String formatted = statisticsService.formatStatistics(stats);
        System.out.println(formatted);
    }
    
    private void showHistory(CommandLine cmd) throws HistoryException {
        logger.info("Showing history");
        
        int limit = Integer.MAX_VALUE;
        if (cmd.hasOption("limit")) {
            try {
                limit = Integer.parseInt(cmd.getOptionValue("limit"));
            } catch (NumberFormatException e) {
                logger.warn("Invalid limit value", e);
            }
        }
        
        List<ListeningHistoryEntry> history = historyService.getRecentHistory(limit);
        
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       LISTENING HISTORY              ║");
        System.out.println("╚══════════════════════════════════════╝\n");
        
        if (history.isEmpty()) {
            System.out.println("No listening history yet. Start by picking an album!");
            return;
        }
        
        System.out.println(String.format("Total: %d albums\n", history.size()));
        
        for (int i = 0; i < history.size(); i++) {
            ListeningHistoryEntry entry = history.get(i);
            System.out.println(String.format("%d. %s - %s (%s)",
                i + 1,
                entry.getArtist(),
                entry.getTitle(),
                entry.getYear()));
            System.out.println(String.format("   Listened: %s",
                entry.getListenedDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            
            if (entry.getRating() != null) {
                System.out.println("   Rating: " + "★".repeat(entry.getRating()));
            }
            
            if (i < history.size() - 1) {
                System.out.println();
            }
        }
    }
    
    private void exportHistory(CommandLine cmd) throws HistoryException {
        String format = cmd.getOptionValue("export", "csv");
        String filename = cmd.getOptionValue("output");
        
        if (filename == null) {
            filename = "listening_history." + format;
        }
        
        logger.info("Exporting history to {} format: {}", format, filename);
        System.out.println("Exporting listening history to " + format.toUpperCase() + "...");
        
        switch (format.toLowerCase()) {
            case "csv":
                exportService.exportToCsv(filename);
                break;
            case "html":
                exportService.exportToHtml(filename);
                break;
            case "markdown":
            case "md":
                exportService.exportToMarkdown(filename);
                break;
            default:
                System.err.println("Unknown export format: " + format);
                System.err.println("Supported formats: csv, html, markdown");
                return;
        }
        
        System.out.println("✅ Exported to: " + filename);
    }
    
    private void showCollectionSize() throws DiscogsApiException {
        logger.info("Showing collection size");
        int size = discogsService.getCollectionSize();
        System.out.println("\n📀 Your Discogs collection has " + size + " albums");
    }
    
    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        DISCOGS RANDOM ALBUM PICKER v" + VERSION + "                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        formatter.printHelp("java -jar discogs-random-picker.jar [OPTIONS]", 
            "\nOptions:", options, 
            "\nExamples:\n" +
            "  Pick random album:          java -jar discogs-random-picker.jar\n" +
            "  Filter by genre:            java -jar discogs-random-picker.jar --genre Rock\n" +
            "  Filter by year:             java -jar discogs-random-picker.jar --year 1980\n" +
            "  Filter by decade:           java -jar discogs-random-picker.jar --min-year 1970 --max-year 1979\n" +
            "  Show statistics:            java -jar discogs-random-picker.jar --stats\n" +
            "  Show history (last 10):     java -jar discogs-random-picker.jar --history --limit 10\n" +
            "  Export to CSV:              java -jar discogs-random-picker.jar --export csv\n" +
            "  Export to HTML:             java -jar discogs-random-picker.jar --export html -o myhistory.html\n");
    }
}
