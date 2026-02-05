# Discogs Random Picker

A Java CLI application that picks random albums from your Discogs collection and tracks your listening history.

## Features

- 🎲 Randomly select albums from your Discogs collection
- 📊 Track listening history in JSON format
- 🎵 Display album information (artist, title, year, format)
- 🔧 Easy configuration via properties file

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Discogs account with API token

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/alfdagos/discogs-random-picker.git
   cd discogs-random-picker
   ```

2. Create your configuration file:
   ```bash
   cp config.properties.example config.properties
   ```

3. Edit `config.properties` and add your Discogs credentials:
   - Get your API token from: https://www.discogs.com/settings/developers
   - Add your Discogs username
   - Add your Discogs API token

4. Build the project:
   ```bash
   mvn clean package
   ```

## Usage

Run the application:
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
```

Or use Maven:
```bash
mvn exec:java -Dexec.mainClass="com.alfdagos.discogsrandompicker.DiscogsRandomPicker"
```

The application will:
1. Connect to your Discogs collection
2. Pick a random album
3. Display album information
4. Ask if you want to mark it as listened
5. Save to listening history if confirmed

## Files

- `DiscogsRandomPicker.java` - Main application source code
- `pom.xml` - Maven project configuration
- `config.properties.example` - Example configuration file
- `listening_history.json` - JSON file storing listening history
- `.gitignore` - Git ignore rules

## License

MIT License
