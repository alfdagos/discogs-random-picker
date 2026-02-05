# Discogs Random Picker - Examples

This file contains practical examples of how to use the Discogs Random Picker application.

## Table of Contents
- [Basic Usage](#basic-usage)
- [Filtering Examples](#filtering-examples)
- [Statistics & History](#statistics--history)
- [Export Examples](#export-examples)
- [Advanced Workflows](#advanced-workflows)

## Basic Usage

### Pick a Random Album

The simplest way to use the app:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar
```

Or using the convenience script:

```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

**Output:**
```
╔════════════════════════════════════════╗
║   🎲 DISCOGS RANDOM ALBUM PICKER 🎲   ║
╚════════════════════════════════════════╝

┌────────────────────────────────────────┐
│           ALBUM INFORMATION            │
└────────────────────────────────────────┘

🎤 Artist:  Pink Floyd
💿 Title:   The Dark Side of the Moon
📅 Year:    1973
📀 Format:  Vinyl, LP, Album
🎵 Genres:  Rock, Prog Rock
🎼 Styles:  Progressive Rock, Psychedelic Rock
🔗 ID:      249396

❓ Mark as listened? (y/n): y

✅ Album added to listening history!
```

## Filtering Examples

### Filter by Genre

Pick a random rock album:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock
```

Pick a random jazz album:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Jazz
```

### Filter by Year

Pick an album from 1980:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --year 1980
```

### Filter by Decade

Pick an album from the 1970s:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --min-year 1970 --max-year 1979
```

Pick an album from the 1960s:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --min-year 1960 --max-year 1969
```

### Filter by Format

Pick a random vinyl album:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --format Vinyl
```

Pick a random CD:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --format CD
```

### Filter by Artist

Pick a random Beatles album:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --artist "The Beatles"
```

### Combine Multiple Filters

Pick a rock vinyl from the 1970s:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock --format Vinyl --min-year 1970 --max-year 1979
```

Pick a specific artist's album from a specific year:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --artist "David Bowie" --year 1977
```

## Statistics & History

### View Statistics

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --stats
```

**Output:**
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

### View Full History

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history
```

### View Recent History (Last 10 Albums)

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --history --limit 10
```

**Output:**
```
╔══════════════════════════════════════╗
║       LISTENING HISTORY              ║
╚══════════════════════════════════════╝

Total: 10 albums

1. Pink Floyd - The Dark Side of the Moon (1973)
   Listened: 2026-02-05 14:32

2. The Beatles - Abbey Road (1969)
   Listened: 2026-02-04 20:15

3. Led Zeppelin - Led Zeppelin IV (1971)
   Listened: 2026-02-03 18:45
```

### Check Collection Size

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --collection-size
```

**Output:**
```
📀 Your Discogs collection has 450 albums
```

## Export Examples

### Export to CSV

Export your entire listening history to CSV:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export csv
```

This creates `listening_history.csv`:
```csv
Discogs ID,Artist,Title,Year,Listened Date,Rating,Notes
249396,"Pink Floyd","The Dark Side of the Moon","1973","2026-02-05 14:32",,
155,"The Beatles","Abbey Road","1969","2026-02-04 20:15",5,
```

### Export to HTML

Export with custom filename:

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export html -o my_music_history.html
```

This creates a styled HTML page with your listening history.

### Export to Markdown

```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export markdown -o HISTORY.md
```

Perfect for including in your GitHub repository!

## Advanced Workflows

### Weekly Album Discovery Routine

Create a batch/shell script for your weekly music discovery:

**Windows (`weekly_discovery.bat`):**
```batch
@echo off
echo === WEEKLY ALBUM DISCOVERY ===
echo.

echo 1. Rock Album from the 70s
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Rock --min-year 1970 --max-year 1979
echo.

echo 2. Jazz Album
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre Jazz
echo.

echo 3. Random Vinyl
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --format Vinyl
echo.

echo Generating statistics...
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --stats

pause
```

**Linux/Mac (`weekly_discovery.sh`):**
```bash
#!/bin/bash
echo "=== WEEKLY ALBUM DISCOVERY ==="
echo

echo "1. Rock Album from the 70s"
./run.sh --genre Rock --min-year 1970 --max-year 1979
echo

echo "2. Jazz Album"
./run.sh --genre Jazz
echo

echo "3. Random Vinyl"
./run.sh --format Vinyl
echo

echo "Generating statistics..."
./run.sh --stats
```

### Monthly Report Generation

Create a script to generate monthly reports:

**`monthly_report.bat`:**
```batch
@echo off
set MONTH=%date:~3,2%
set YEAR=%date:~6,4%

echo Generating monthly report for %MONTH%-%YEAR%

java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export html -o reports\%YEAR%-%MONTH%-report.html
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --export csv -o reports\%YEAR%-%MONTH%-report.csv
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --stats > reports\%YEAR%-%MONTH%-stats.txt

echo Report generated in reports folder
pause
```

### Themed Listening Sessions

**80s Night:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --min-year 1980 --max-year 1989
```

**Prog Rock Session:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --genre "Progressive Rock"
```

**Vinyl Only:**
```bash
java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar --format Vinyl
```

### Integration with Other Tools

**Open album on Discogs (Windows):**
```batch
@echo off
for /f "tokens=2 delims=:" %%a in ('java -jar target/discogs-random-picker-1.0-SNAPSHOT.jar ^| findstr "ID:"') do (
    start https://www.discogs.com/release/%%a
)
```

**Share on social media (Linux):**
```bash
#!/bin/bash
ALBUM=$(./run.sh | grep "Title:" | cut -d: -f2)
echo "Currently listening to:$ALBUM #NowPlaying #Discogs" | xclip -selection clipboard
```

## Tips & Tricks

### Create Shell Aliases

Add to your `.bashrc` or `.bash_profile`:

```bash
alias drp='cd ~/discogs-random-picker && ./run.sh'
alias drp-stats='cd ~/discogs-random-picker && ./run.sh --stats'
alias drp-history='cd ~/discogs-random-picker && ./run.sh --history --limit 10'
alias drp-70s='cd ~/discogs-random-picker && ./run.sh --min-year 1970 --max-year 1979'
```

### Windows Shortcuts

Create desktop shortcuts with different parameters:
- Right-click desktop → New → Shortcut
- Target: `java -jar "C:\path\to\discogs-random-picker-1.0-SNAPSHOT.jar" --genre Rock`
- Name: `Pick Random Rock Album`

### Cron Job for Daily Album

Add to crontab for daily album suggestion:

```bash
0 9 * * * cd /home/user/discogs-random-picker && ./run.sh >> daily-albums.log 2>&1
```

---

For more information, see the main [README.md](README.md).
