# Discogs Random Picker

🎲 A powerful Java CLI application that picks random albums from your Discogs collection and tracks your listening history with advanced features.

## ✨ Features

### Core Features
- 🎲 **Random Album Selection** - Randomly pick albums from your Discogs collection
- 🔍 **Advanced Filtering** - Filter by genre, year, format, artist, or decade
- 📊 **Listening History** - Track all albums you've listened to with timestamps
- 📈 **Statistics Dashboard** - View comprehensive stats about your listening habits
- 📤 **Export Options** - Export history to CSV, HTML, or Markdown formats
- 🚫 **Duplicate Detection** - Automatically detects if you've already listened to an album
- 💾 **Persistent Storage** - All history saved in JSON format

### Technical Features
- ⚡ **Professional Logging** - SLF4J + Logback for comprehensive logging
- 🏗️ **Clean Architecture** - Separated into models, services, and configuration layers
- ✅ **Well Tested** - Includes JUnit 5 unit tests with high coverage
- 🛡️ **Robust Error Handling** - Custom exceptions for better error management
- 🔄 **Rate Limiting** - Automatic retry logic for Discogs API calls
- 📝 **Extensive Documentation** - JavaDoc comments throughout

## 🚀 Quick Start

### Prerequisites

- **Java 11 or higher**
- **Maven 3.6 or higher**
- **Discogs account with API token**

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/alfdagos/discogs-random-picker.git
   cd discogs-random-picker
   ```

2. **Create configuration file:**
   ```bash
   cp config.properties.example config.properties
   ```

3. **Configure your credentials:**
   
   Edit `config.properties` and add:
   - Get your API token from: https://www.discogs.com/settings/developers
   - Add your Discogs username
   - Add your Discogs API token

4. **Build the project:**
   ```bash
   mvn clean package
   ```

## 📖 Usage

### Basic Usage

Pick a random album (no arguments):
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
```

Or with Maven:
```bash
mvn exec:java -Dexec.mainClass="com.alfdagos.discogsrandompicker.DiscogsRandomPicker"
```

### Advanced Usage

#### Filter Options

**Filter by genre:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock
```

**Filter by specific year:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --year 1980
```

**Filter by decade:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --min-year 1970 --max-year 1979
```

**Filter by format:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --format Vinyl
```

**Filter by artist:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --artist "Pink Floyd"
```

**Combine multiple filters:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock --min-year 1970 --max-year 1979 --format Vinyl
```

#### History & Statistics

**View listening statistics:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --stats
```

**View listening history:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history
```

**View last 10 entries:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history --limit 10
```

**Check collection size:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --collection-size
```

#### Export Options

**Export to CSV:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export csv
```

**Export to HTML:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export html -o myhistory.html
```

**Export to Markdown:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export markdown
```

### All Command Line Options

```
-h, --help              Show help message
-v, --version           Show version
-s, --stats             Show listening statistics
    --history           Show listening history
    --collection-size   Show collection size
    --limit <NUMBER>    Limit number of history entries
-g, --genre <GENRE>     Filter by genre
-y, --year <YEAR>       Filter by year
-f, --format <FORMAT>   Filter by format (Vinyl, CD, etc.)
-a, --artist <ARTIST>   Filter by artist name
    --min-year <YEAR>   Minimum year for range filter
    --max-year <YEAR>   Maximum year for range filter
-e, --export <FORMAT>   Export history (csv, html, markdown)
-o, --output <FILE>     Output filename for export
    --no-duplicate      Skip albums already in history
```

## 📁 Project Structure

```
discogs-random-picker/
├── src/
│   ├── main/
│   │   ├── java/com/alfdagos/discogsrandompicker/
│   │   │   ├── DiscogsRandomPicker.java       # Main application
│   │   │   ├── config/
│   │   │   │   └── ConfigManager.java         # Configuration management
│   │   │   ├── exception/
│   │   │   │   ├── ConfigurationException.java
│   │   │   │   ├── DiscogsApiException.java
│   │   │   │   └── HistoryException.java
│   │   │   ├── model/
│   │   │   │   ├── Album.java                 # Album entity
│   │   │   │   ├── AlbumFilter.java           # Filter criteria
│   │   │   │   ├── ListeningHistoryEntry.java # History entry
│   │   │   │   └── Statistics.java            # Statistics model
│   │   │   └── service/
│   │   │       ├── DiscogsService.java        # Discogs API interaction
│   │   │       ├── HistoryService.java        # History management
│   │   │       ├── StatisticsService.java     # Statistics generation
│   │   │       └── ExportService.java         # Export functionality
│   │   └── resources/
│   │       └── logback.xml                    # Logging configuration
│   └── test/
│       └── java/com/alfdagos/discogsrandompicker/
│           ├── model/
│           │   ├── AlbumTest.java
│           │   └── AlbumFilterTest.java
│           └── service/
│               └── HistoryServiceTest.java
├── config.properties.example                  # Example configuration
├── listening_history.json                     # Listening history (auto-generated)
├── pom.xml                                    # Maven configuration
└── README.md                                  # This file
```

## 🔧 Configuration

Create a `config.properties` file in the project root:

```properties
# Discogs API Configuration
# Get your token from: https://www.discogs.com/settings/developers
discogs.username=your_discogs_username
discogs.token=your_discogs_api_token

# Spotify API Configuration (Optional - for future integration)
spotify.client.id=your_spotify_client_id
spotify.client.secret=your_spotify_client_secret
```

## 📊 Statistics Example

The statistics feature provides insights like:

```
╔══════════════════════════════════════╗
║     LISTENING HISTORY STATISTICS     ║
╚══════════════════════════════════════╝

📀 Total Albums Listened: 142

🎤 Most Listened Artist: Pink Floyd (8 albums)
📅 Most Popular Year: 1977 (12 albums)
🕰️  Most Popular Decade: 1970s (58 albums)

🎸 Top 5 Artists:
   Pink Floyd: 8 albums
   The Beatles: 7 albums
   Led Zeppelin: 6 albums
   David Bowie: 5 albums
   Radiohead: 4 albums

📆 Top 5 Years:
   1977: 12 albums
   1973: 10 albums
   1971: 9 albums
   1980: 8 albums
   1975: 8 albums
```

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

Run with coverage:

```bash
mvn clean test jacoco:report
```

## 📝 Logging

Logs are stored in `logs/discogs-random-picker.log` and rotated daily. Configure logging levels in [src/main/resources/logback.xml](src/main/resources/logback.xml).

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

MIT License

## 🙏 Acknowledgments

- [Discogs API](https://www.discogs.com/developers) for providing access to music data
- [Gson](https://github.com/google/gson) for JSON processing
- [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) for command-line parsing
- [SLF4J](https://www.slf4j.org/) and [Logback](https://logback.qos.ch/) for logging

## 🔮 Future Features

- 🎧 Spotify integration for direct playback
- 🌐 Web interface for browsing history
- 📱 REST API for mobile integration
- 🎨 Album cover display in terminal
- 🔔 Notifications when new albums are added to collection
- 📊 Advanced analytics and charts
- 🎯 Recommendation system based on listening history

## 💡 Tips & Tricks

1. **Create shortcuts** - Add aliases to your shell for common commands
2. **Regular exports** - Export your history regularly as backup
3. **Use filters wisely** - If a filter returns no results after 50 attempts, try relaxing your criteria
4. **Check collection size** - Use `--collection-size` to see how many albums you have

## 🐛 Troubleshooting

### "Configuration Error: Discogs username not configured"
Make sure you've created `config.properties` from the example file and filled in your credentials.

### "API request failed with status code: 401"
Your Discogs token is invalid. Get a new one from https://www.discogs.com/settings/developers

### "Rate limited after X retries"
The application respects Discogs API rate limits. Wait a moment and try again.

## 📞 Support

If you encounter any issues or have questions:
- Open an issue on GitHub
- Check existing issues for solutions
- Review the logs in `logs/discogs-random-picker.log`

---

**Made with ❤️ by alfdagos**

Version 2.0 - Enhanced Edition
