# 🎵 Discogs Random Album Picker

A Java CLI application that picks random albums from your Discogs collection, searches for them on Spotify, and maintains a listening history.

## ✨ Features

- 🎲 **Random Album Selection**: Fetches a random album from your Discogs collection
- 🔍 **Spotify Integration**: Automatically searches for the album on Spotify
- 📊 **Rich Information Display**: Shows album details including artist, year, format, genres, and styles
- 💾 **Listening History**: Maintains a persistent JSON history of all picked albums
- 📜 **History View**: Display your listening history with the `--history` flag
- 🎨 **Beautiful CLI Output**: Emoji-enhanced, UTF-8 formatted terminal output
- ⚙️ **Flexible Configuration**: Supports both configuration files and environment variables
- 🔄 **Retry Logic**: Built-in retry mechanism for API calls with rate limit handling
- 🔗 **Direct Links**: Provides direct links to albums on both Discogs and Spotify

## 📋 Requirements

- **Java 11** or higher
- **Maven 3.6+** for building
- **Discogs Account** with a personal access token
- **Spotify Developer Account** with a registered application

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/alfdagos/discogs-random-picker.git
cd discogs-random-picker
```

### 2. Set Up API Credentials

#### Option A: Using Configuration File (Recommended)

```bash
cp config.properties.example config.properties
```

Edit `config.properties` and add your credentials:

```properties
discogs.token=YOUR_DISCOGS_TOKEN
discogs.username=YOUR_DISCOGS_USERNAME
spotify.client.id=YOUR_SPOTIFY_CLIENT_ID
spotify.client.secret=YOUR_SPOTIFY_CLIENT_SECRET
```

#### Option B: Using Environment Variables

```bash
export DISCOGS_TOKEN="your_discogs_token"
export DISCOGS_USERNAME="your_discogs_username"
export SPOTIFY_CLIENT_ID="your_spotify_client_id"
export SPOTIFY_CLIENT_SECRET="your_spotify_client_secret"
```

### 3. Build the Application

```bash
mvn clean package
```

This creates an executable JAR file with all dependencies included:
- `target/discogs-random-picker-1.0.0-jar-with-dependencies.jar`

### 4. Run the Application

```bash
java -jar target/discogs-random-picker-1.0.0-jar-with-dependencies.jar
```

## 🔐 API Authentication Setup

### Discogs API

1. Go to [Discogs Developer Settings](https://www.discogs.com/settings/developers)
2. Click **Generate new token** under "Personal access tokens"
3. Copy the generated token
4. Your Discogs username is visible on your profile page

### Spotify API

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Click **Create an App**
3. Fill in the app name and description (e.g., "Discogs Random Picker")
4. Accept the terms and click **Create**
5. Click **Show Client Secret**
6. Copy both **Client ID** and **Client Secret**

**Note**: This application uses the Spotify Client Credentials flow, which doesn't require user authentication. It only searches for albums, not accessing any user data.

## 💻 Usage

### Pick a Random Album

Simply run the application without arguments:

```bash
java -jar target/discogs-random-picker-1.0.0-jar-with-dependencies.jar
```

### View Listening History

Use the `--history` flag to see all previously picked albums:

```bash
java -jar target/discogs-random-picker-1.0.0-jar-with-dependencies.jar --history
```

### Debug Mode

Enable debug mode for detailed error messages:

```bash
DEBUG=true java -jar target/discogs-random-picker-1.0.0-jar-with-dependencies.jar
```

## 📄 Sample Output

### Random Album Pick

```
🎵 Discogs Random Album Picker 🎵

📚 Fetching your Discogs collection...
   Found 342 albums in your collection!

🎲 Picking a random album...
   Selected: Pink Floyd - The Dark Side of the Moon

🔍 Searching on Spotify...
   ✅ Found on Spotify!

╔════════════════════════════════════════════════════════════╗
║              🎼 YOUR RANDOM ALBUM PICK 🎼                 ║
╚════════════════════════════════════════════════════════════╝

  🎤 Artist:  Pink Floyd
  💿 Album:   The Dark Side of the Moon
  📅 Year:    1973
  📀 Format:  Vinyl (LP, Album)
  🎸 Genres:  Rock
  🎹 Styles:  Progressive Rock, Psychedelic Rock

  🔗 Links:
     Discogs:  https://www.discogs.com/release/123456
     Spotify:  https://open.spotify.com/album/4LH4d3cOWNNsVw41Gqt2kv

💾 Saved to listening history!

✨ Enjoy your music! ✨
```

### History View

```
📜 Listening History (5 albums)

════════════════════════════════════════════════════════════

1. Pink Floyd - The Dark Side of the Moon
   Year: 1973
   Picked: 2026-02-05 11:30
   Spotify: https://open.spotify.com/album/4LH4d3cOWNNsVw41Gqt2kv

2. Miles Davis - Kind of Blue
   Year: 1959
   Picked: 2026-02-04 15:22
   Spotify: https://open.spotify.com/album/1weenld61qoidwYuZ1GESA

...

════════════════════════════════════════════════════════════
```

## 🏗️ Project Structure

```
discogs-random-picker/
├── src/
│   └── main/
│       └── java/
│           └── DiscogsRandomPicker.java    # Main application
├── target/                                  # Build output (created by Maven)
├── pom.xml                                  # Maven configuration
├── config.properties.example                # Configuration template
├── config.properties                        # Your config (git-ignored)
├── listening_history.json                   # Listening history (git-ignored)
├── .gitignore                              # Git ignore rules
└── README.md                               # This file
```

## 🔧 Configuration Options

You can customize the application behavior in `config.properties`:

```properties
# Maximum number of retries for API calls (default: 3)
api.max.retries=3

# Delay between retries in milliseconds (default: 1000)
api.retry.delay=1000
```

## 🛠️ Development

### Build Without Running

```bash
mvn clean compile
```

### Run Tests (if added)

```bash
mvn test
```

### Clean Build Artifacts

```bash
mvn clean
```

### Create Distribution Package

```bash
mvn clean package
```

## 📦 Dependencies

- **Gson 2.10.1**: JSON parsing and serialization
- **Java 11 Standard Library**: HTTP connections, I/O operations

No external HTTP client libraries required - uses Java's built-in `HttpURLConnection`.

## 🐛 Troubleshooting

### "Missing configuration" Error

**Problem**: `Missing configuration: discogs.token`

**Solution**: 
1. Make sure you've created `config.properties` from the example file
2. Verify that your API credentials are correctly filled in
3. Check that the file is in the same directory as the JAR file

### "Discogs API rate limit exceeded"

**Problem**: Too many API requests in a short time

**Solution**:
1. Wait a few minutes before trying again
2. Discogs allows 60 requests per minute for authenticated users
3. The application includes retry logic that handles this automatically

### "Spotify authentication failed"

**Problem**: Invalid Spotify credentials

**Solution**:
1. Verify your Client ID and Client Secret are correct
2. Make sure there are no extra spaces in the configuration
3. Check that your Spotify app is not in development mode restrictions

### "Album not found on Spotify"

**Problem**: The album exists on Discogs but not on Spotify

**Solution**:
- This is normal! Not all albums on Discogs are available on Spotify
- The application will still display album information and Discogs link
- Rare, independent, or regional releases may not be on Spotify

### UTF-8/Emoji Display Issues

**Problem**: Emojis appear as question marks or boxes

**Solution**:
1. Ensure your terminal supports UTF-8 encoding
2. On Windows, use Windows Terminal or enable UTF-8 support:
   ```bash
   chcp 65001
   ```
3. Set Java encoding explicitly:
   ```bash
   java -Dfile.encoding=UTF-8 -jar target/discogs-random-picker-1.0.0-jar-with-dependencies.jar
   ```

### Empty Collection Error

**Problem**: "No albums found in your collection"

**Solution**:
1. Verify your Discogs username is correct
2. Make sure you have albums in your collection on Discogs
3. Check that your token has the correct permissions

## 📝 Data Files

### listening_history.json

The application maintains a JSON file with your listening history:

```json
[
  {
    "artist": "Pink Floyd",
    "title": "The Dark Side of the Moon",
    "year": 1973,
    "format": "Vinyl",
    "formatDetails": "LP, Album",
    "genres": "Rock",
    "styles": "Progressive Rock, Psychedelic Rock",
    "discogsUrl": "https://www.discogs.com/release/123456",
    "spotifyUrl": "https://open.spotify.com/album/4LH4d3cOWNNsVw41Gqt2kv",
    "timestamp": "2026-02-05T11:30:00Z"
  }
]
```

The history is limited to the last 100 entries and is automatically maintained.

## 🤝 Contributing

Contributions are welcome! Feel free to:

1. Report bugs or issues
2. Suggest new features
3. Submit pull requests

## 📜 License

This project is open source and available under the MIT License.

## 🙏 Acknowledgments

- **Discogs** for their comprehensive music database and API
- **Spotify** for their music streaming platform and API
- All music enthusiasts who enjoy discovering random albums from their collection

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Troubleshooting](#-troubleshooting) section
2. Review [Discogs API Documentation](https://www.discogs.com/developers)
3. Review [Spotify API Documentation](https://developer.spotify.com/documentation/web-api)
4. Open an issue on GitHub

---

**Happy listening! 🎧**
