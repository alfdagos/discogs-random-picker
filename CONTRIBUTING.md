# Contributing to Discogs Random Picker

First off, thank you for considering contributing to Discogs Random Picker! 🎉

## Code of Conduct

This project adheres to a code of conduct. By participating, you are expected to uphold this code. Please be respectful and constructive in all interactions.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples** (code snippets, config files)
- **Describe the behavior you observed and what you expected**
- **Include logs** from `logs/discogs-random-picker.log`
- **Include your environment** (OS, Java version, Maven version)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion:

- **Use a clear and descriptive title**
- **Provide a detailed description** of the suggested enhancement
- **Explain why this enhancement would be useful**
- **List any alternative solutions** you've considered

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Follow the code style** (see below)
3. **Add tests** for any new functionality
4. **Update documentation** (README, JavaDoc, CHANGELOG)
5. **Ensure all tests pass** (`mvn test`)
6. **Create a pull request** with a clear description

## Development Setup

1. **Fork and clone the repository:**
   ```bash
   git clone https://github.com/YOUR-USERNAME/discogs-random-picker.git
   cd discogs-random-picker
   ```

2. **Create configuration file:**
   ```bash
   cp config.properties.example config.properties
   # Edit config.properties with your credentials
   ```

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run tests:**
   ```bash
   mvn test
   ```

## Code Style Guidelines

### Java Code

- **Follow Java naming conventions**:
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

- **Formatting**:
  - Indentation: 4 spaces (no tabs)
  - Line length: 120 characters maximum
  - Braces: Always use braces, even for single-line blocks

- **Documentation**:
  - All public classes and methods must have JavaDoc
  - Complex logic should have inline comments
  - Use `@param`, `@return`, `@throws` tags appropriately

### Example:

```java
/**
 * Service for managing album filters.
 * 
 * This service provides functionality to create and apply
 * filters to album collections.
 */
public class FilterService {
    
    private static final Logger logger = LoggerFactory.getLogger(FilterService.class);
    
    /**
     * Applies the given filter to an album.
     * 
     * @param album the album to filter
     * @param filter the filter criteria
     * @return true if the album matches the filter, false otherwise
     * @throws FilterException if the filter cannot be applied
     */
    public boolean applyFilter(Album album, AlbumFilter filter) throws FilterException {
        logger.debug("Applying filter to album: {}", album.getTitle());
        // Implementation
    }
}
```

## Project Structure

When adding new features, maintain the existing structure:

```
src/main/java/com/alfdagos/discogsrandompicker/
├── DiscogsRandomPicker.java          # Main class
├── config/                           # Configuration management
├── exception/                        # Custom exceptions
├── model/                            # Data models
└── service/                          # Business logic
```

## Testing

- Write tests for all new functionality
- Use JUnit 5 and Mockito
- Aim for high test coverage (>80%)
- Test edge cases and error conditions

### Test Structure:

```java
@Test
void testMethodName_Scenario_ExpectedBehavior() {
    // Arrange
    Album album = new Album.Builder()
        .withTitle("Test Album")
        .build();
    
    // Act
    String result = album.getTitle();
    
    // Assert
    assertEquals("Test Album", result);
}
```

## Commit Messages

Use clear and meaningful commit messages:

- **Format**: `type: subject`
- **Types**: 
  - `feat:` New feature
  - `fix:` Bug fix
  - `docs:` Documentation changes
  - `test:` Adding/updating tests
  - `refactor:` Code refactoring
  - `style:` Code style changes
  - `chore:` Maintenance tasks

### Examples:
```
feat: add export to PDF functionality
fix: resolve rate limiting issue in DiscogsService
docs: update README with new filter options
test: add tests for StatisticsService
```

## Branching Strategy

- `main` - Stable, production-ready code
- `develop` - Integration branch for features
- `feature/feature-name` - New features
- `fix/bug-description` - Bug fixes
- `docs/documentation-update` - Documentation changes

## Release Process

1. Update version in `pom.xml`
2. Update `CHANGELOG.md`
3. Update `README.md` if needed
4. Create a pull request to `main`
5. Tag the release after merge

## Questions?

Feel free to open an issue with the `question` label if you have any questions about contributing.

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (MIT License).

---

Thank you for contributing! 🙏
