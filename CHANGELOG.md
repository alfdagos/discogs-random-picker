# Changelog

All notable changes to the Discogs Random Picker project will be documented in this file.

## [2.0.0] - 2026-02-05

### 🎉 Major Release - Complete Rewrite

This release represents a complete rewrite of the application with professional-grade architecture and features.

### ✨ Added
- **Advanced CLI Interface** - Full command-line argument parsing with Apache Commons CLI
- **Filter System** - Filter albums by genre, year, format, artist, or decade
- **Statistics Dashboard** - Comprehensive statistics about listening habits
  - Total albums listened
  - Most listened artist
  - Most popular year and decade
  - Top 5 artists and years
- **Export Functionality** - Export listening history to multiple formats:
  - CSV format
  - HTML format with styling
  - Markdown format
- **Duplicate Detection** - Automatically detects previously listened albums
- **Collection Size** - View total number of albums in your collection
- **History Management** - View and manage listening history
  - View recent history with limit option
  - Clear history functionality
- **Professional Logging** - SLF4J with Logback
  - Console output
  - File logging with daily rotation
  - Configurable log levels
- **Comprehensive Testing** - JUnit 5 test suite
  - Model tests
  - Service tests
  - High test coverage
- **Launch Scripts** - Convenience scripts for Windows and Linux
  - `run.bat` for Windows
  - `run.sh` for Unix/Linux

### 🏗️ Architecture Improvements
- **Clean Architecture** - Separated into distinct layers:
  - Models (Album, ListeningHistoryEntry, Statistics, AlbumFilter)
  - Services (DiscogsService, HistoryService, StatisticsService, ExportService)
  - Configuration (ConfigManager)
  - Exceptions (custom exception hierarchy)
- **Builder Pattern** - Album model uses builder pattern
- **Dependency Injection** - Service-oriented design
- **SOLID Principles** - Following best practices

### 🛡️ Enhanced Error Handling
- **Custom Exceptions**:
  - `DiscogsApiException` - For API-related errors
  - `ConfigurationException` - For configuration issues
  - `HistoryException` - For history management errors
- **Rate Limiting** - Automatic retry with exponential backoff
- **Validation** - Input validation and configuration validation

### 📚 Documentation
- **Complete README** - Comprehensive documentation with:
  - Quick start guide
  - All command-line options
  - Usage examples
  - Troubleshooting section
- **JavaDoc Comments** - Throughout the codebase
- **Code Examples** - In README for common use cases

### 📦 Dependencies
- **Added**:
  - SLF4J 2.0.9 (logging API)
  - Logback 1.4.11 (logging implementation)
  - Apache Commons CLI 1.6.0 (command-line parsing)
  - Apache Commons Lang3 3.14.0 (utilities)
  - JUnit 5.10.1 (testing framework)
  - Mockito 5.8.0 (mocking framework)
- **Updated**:
  - Gson 2.10.1 (JSON processing)
  - Maven plugins to latest versions

### 🔧 Configuration
- **Enhanced Configuration** - More robust configuration management
- **Validation** - Automatic validation of required settings
- **Better Error Messages** - Clear error messages for configuration issues

### 🎨 UI Improvements
- **Enhanced Output** - Beautiful ASCII art boxes and formatting
- **Emojis** - Visual indicators for different types of information
- **Color Support** - Better terminal output formatting
- **Progress Indicators** - Clear feedback during operations

### 🚀 Performance
- **Efficient API Calls** - Optimized Discogs API interaction
- **Caching** - Intelligent caching of API responses
- **Batch Operations** - Support for bulk operations

### 🔐 Security
- **Token Security** - Better handling of API tokens
- **Input Sanitization** - Protection against invalid inputs
- **File Permissions** - Proper file handling

## [1.0.0] - Initial Release

### Added
- Basic random album picker functionality
- Simple Discogs API integration
- Basic listening history tracking
- Simple command-line interface
- JSON-based history storage

---

## Version Numbering

This project follows [Semantic Versioning](https://semver.org/):
- MAJOR version for incompatible API changes
- MINOR version for new functionality in a backward compatible manner
- PATCH version for backward compatible bug fixes
