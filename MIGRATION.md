# 🔄 Migration Guide: v1.0 → v2.0

This guide helps existing users migrate from Discogs Random Picker v1.0 to v2.0.

## 🎯 Quick Summary

v2.0 is **backward compatible** with v1.0 configuration files. Your existing `config.properties` and `listening_history.json` files will continue to work without any changes.

## ✅ What Works Out of the Box

### Configuration File
Your existing `config.properties` is fully compatible:
```properties
discogs.username=your_username
discogs.token=your_token
```

No changes needed!

### Listening History
Your existing `listening_history.json` will be automatically read and used by v2.0.

### Basic Usage
The basic command still works exactly the same:
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
```

## 🆕 New Features You Can Use Immediately

### 1. Command Line Options

**v1.0 Way:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
# Only this command available
```

**v2.0 Way:**
```bash
# Same basic command
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar

# Plus new options:
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --stats
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock
```

### 2. Convenience Scripts

**v1.0 Way:**
```bash
# Full Java command every time
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
```

**v2.0 Way:**
```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

### 3. View Your Statistics

**New in v2.0:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --stats
```

See all your listening history analyzed with:
- Total albums listened
- Most listened artist
- Most popular year/decade
- Top 5 artists and years

### 4. View History

**New in v2.0:**
```bash
# View all history
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history

# View last 10 entries
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history --limit 10
```

### 5. Export Your History

**New in v2.0:**
```bash
# Export to CSV
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export csv

# Export to HTML
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export html

# Export to Markdown
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export markdown
```

### 6. Filter Albums

**New in v2.0:**
```bash
# By genre
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock

# By year
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --year 1980

# By decade
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --min-year 1970 --max-year 1979

# By format
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --format Vinyl

# Combine filters
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock --format Vinyl --min-year 1970 --max-year 1979
```

## 🔧 Breaking Changes

### None! 🎉

v2.0 is 100% backward compatible with v1.0. All your existing:
- Configuration files ✅
- History data ✅
- Workflow ✅

...will continue to work without any modifications.

## 📦 Update Steps

### 1. Backup Your Data (Recommended)

Before updating, backup your data:

```bash
# Windows
copy config.properties config.properties.backup
copy listening_history.json listening_history.json.backup

# Linux/Mac
cp config.properties config.properties.backup
cp listening_history.json listening_history.json.backup
```

### 2. Pull Latest Code

```bash
git pull origin main
```

### 3. Rebuild Project

```bash
mvn clean package
```

### 4. Test Basic Functionality

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --help
```

You should see the new help menu with all available options.

### 5. Verify Your History

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history
```

Your existing listening history should be displayed correctly.

## 🎓 Learning the New Features

### Week 1: Get Familiar with CLI
```bash
# Day 1: Try help
./run.sh --help

# Day 2: View your stats
./run.sh --stats

# Day 3: View your history
./run.sh --history --limit 10

# Day 4: Try a simple filter
./run.sh --genre Rock

# Day 5: Export your history
./run.sh --export html
```

### Week 2: Advanced Features
```bash
# Day 1: Multiple filters
./run.sh --genre Rock --min-year 1970 --max-year 1979

# Day 2: Export all formats
./run.sh --export csv
./run.sh --export html
./run.sh --export markdown

# Day 3: Check collection size
./run.sh --collection-size

# Day 4: Create custom scripts
# See EXAMPLES.md for ideas

# Day 5: Set up automated workflows
# See EXAMPLES.md for automation ideas
```

## 📋 Feature Comparison Checklist

| Feature | v1.0 | v2.0 | Migration Notes |
|---------|------|------|----------------|
| Random pick | ✅ | ✅ | Same behavior |
| Configuration file | ✅ | ✅ | Same format |
| History tracking | ✅ | ✅ | Same file format |
| Display album info | ✅ | ✅ | Enhanced in v2.0 |
| Mark as listened | ✅ | ✅ | Same behavior |
| CLI arguments | ❌ | ✅ | **NEW** |
| Statistics | ❌ | ✅ | **NEW** |
| Export | ❌ | ✅ | **NEW** |
| Filters | ❌ | ✅ | **NEW** |
| Logging | Basic | Advanced | Automatic upgrade |
| Error handling | Basic | Advanced | Automatic upgrade |
| Testing | ❌ | ✅ | **NEW** |
| Documentation | Basic | Complete | **NEW** |

## 🐛 Troubleshooting

### Issue: "Configuration Error"

**Solution:** Your config file format is correct, but you may need to check:
```bash
# Verify file exists
ls config.properties

# Verify content
cat config.properties
```

### Issue: "History file format error"

**Solution:** v2.0 auto-migrates v1.0 history files. If you see errors:
```bash
# Check file exists
ls listening_history.json

# Check file is valid JSON
cat listening_history.json
```

### Issue: "Build errors"

**Solution:** v2.0 has new dependencies. Rebuild:
```bash
mvn clean install -U
```

## 📚 Learning Resources

### Essential Reading
1. [README.md](README.md) - Complete documentation
2. [EXAMPLES.md](EXAMPLES.md) - Practical examples
3. [CHANGELOG.md](CHANGELOG.md) - What's new

### Advanced Topics
4. [CONTRIBUTING.md](CONTRIBUTING.md) - If you want to contribute
5. [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) - Technical overview

## 💡 Tips for v1.0 Users

### 1. Start Simple
Don't try to learn all features at once. Start with:
```bash
./run.sh --stats
./run.sh --history
```

### 2. Create Aliases
Make your life easier:
```bash
# Add to your .bashrc or .bash_profile
alias drp='cd ~/discogs-random-picker && ./run.sh'
alias drp-stats='cd ~/discogs-random-picker && ./run.sh --stats'
```

### 3. Use the Help
When in doubt:
```bash
./run.sh --help
```

### 4. Check Logs
If something goes wrong, check:
```bash
cat logs/discogs-random-picker.log
```

### 5. Export Regularly
Back up your history:
```bash
./run.sh --export csv
```

## 🎉 Welcome to v2.0!

You're now ready to enjoy all the new features of Discogs Random Picker v2.0!

### Quick Start Commands

```bash
# Your old command still works
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar

# But now you can also:
./run.sh                          # Same as above
./run.sh --stats                  # View statistics
./run.sh --history --limit 10     # View recent history
./run.sh --genre Rock             # Filter by genre
./run.sh --export html            # Export to HTML
./run.sh --help                   # See all options
```

### Need Help?

- Check [README.md](README.md) for documentation
- Check [EXAMPLES.md](EXAMPLES.md) for examples
- Check logs in `logs/` directory
- Open an issue on GitHub

---

**Happy listening! 🎵**
