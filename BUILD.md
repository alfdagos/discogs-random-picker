# Build Instructions

## Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Building from Source

1. **Clone the repository** (if not already done):
   ```bash
   git clone <repository-url>
   cd discogs-random-picker
   ```

2. **Build the project**:
   ```bash
   mvn clean package -DskipTests
   ```
   
   Or using the local settings (if you have repository configuration issues):
   ```bash
   mvn clean package -s .mvn\settings.xml -DskipTests
   ```

3. **The executable JAR will be created in**:
   ```
   target/discogs-random-picker-1.0-SNAPSHOT.jar
   ```

## Running the Application

### First Time Setup

1. **Create configuration file**:
   ```bash
   copy config.properties.example config.properties  # Windows
   cp config.properties.example config.properties    # Linux/Mac
   ```

2. **Edit config.properties** and add your Discogs credentials:
   ```properties
   discogs.username=YOUR_USERNAME
   discogs.token=YOUR_PERSONAL_ACCESS_TOKEN
   ```

### Running

**View help**:
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --help
```

**Pick a random album**:
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
```

**With filters**:
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock --year 1980
```

## Using the Launch Scripts

For convenience, you can use the provided scripts:

**Windows**:
```cmd
run.bat
run.bat --genre Rock
run.bat --stats
```

**Linux/Mac**:
```bash
chmod +x run.sh
./run.sh
./run.sh --genre Rock
./run.sh --stats
```

## Development Build

### Compile only (no JAR):
```bash
mvn compile
```

### Run tests:
```bash
mvn test
```

### Full build with tests:
```bash
mvn clean package
```

## Troubleshooting

### Repository Access Issues

If you encounter errors like:
```
Could not transfer artifact...from/to nexus-inps
```

Use the local Maven settings:
```bash
mvn clean package -s .mvn\settings.xml -DskipTests
```

### Java Version Issues

Check your Java version:
```bash
java -version
```

The project requires Java 11 or higher.

### Maven Version Issues

Check your Maven version:
```bash
mvn -version
```

The project requires Maven 3.6 or higher.

## Project Structure

```
discogs-random-picker/
├── .mvn/
│   └── settings.xml          # Local Maven configuration
├── src/
│   ├── main/
│   │   ├── java/             # Application source code
│   │   └── resources/        # Configuration files
│   └── test/
│       └── java/             # Test code
├── target/                   # Build output
├── pom.xml                   # Maven configuration
├── config.properties.example # Configuration template
├── run.bat                   # Windows launcher
├── run.sh                    # Linux/Mac launcher
└── BUILD.md                  # This file
```

## Dependencies

The project uses:
- **Gson 2.10.1**: JSON parsing
- **SLF4J 2.0.9**: Logging API
- **Logback 1.4.11**: Logging implementation
- **Apache Commons CLI 1.6.0**: Command-line parsing
- **Apache Commons Lang3 3.14.0**: Utility functions
- **JUnit 5.10.1**: Testing framework
- **Mockito 5.8.0**: Mocking framework

All dependencies are automatically downloaded by Maven during the build process.

## Clean Build

To start fresh:
```bash
mvn clean
```

To clean and rebuild:
```bash
mvn clean compile
mvn clean package
```

## Build Profiles

The project uses Maven Shade Plugin to create an "uber-jar" with all dependencies included.

The final JAR is self-contained and can be distributed as a single file.

## Advanced Options

### Skip Tests
```bash
mvn package -DskipTests
```

### Verbose Output
```bash
mvn package -X
```

### Specific Java Version
```bash
mvn package -Djava.version=11
```

## Next Steps

After building:
1. See [README.md](README.md) for usage instructions
2. See [EXAMPLES.md](EXAMPLES.md) for practical examples
3. See [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines
