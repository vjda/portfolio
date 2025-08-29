# Portfolio Performance CLI

A command-line interface for Portfolio Performance that allows users to read and query portfolio data without the graphical interface.

## Architecture

The CLI is implemented as a standalone Java application that:

- **Uses picocli for command parsing**: Professional CLI framework with automatic help generation
- **Uses Jackson for JSON serialization**: Robust JSON handling instead of custom implementation  
- **Standalone fat jar**: Single executable JAR (7.6MB) with all dependencies included
- **Java 17+ compatible**: No longer requires Java 21 or Eclipse/OSGi runtime
- **Reuses Portfolio domain concepts**: Compatible with Portfolio Performance XML format

## Building

The CLI requires Java 17+ to build and run.

### Build the fat jar:

```bash
cd name.abuchen.portfolio.cli
mvn clean compile package -DskipTests
```

This creates `target/portfolio-cli.jar` containing all dependencies.

### Shell launcher:

The `pp` script in the root directory provides an easy way to run the CLI:

```bash
# The script will check for the fat jar and provide build instructions if missing
./pp --help
```

## Usage

### Basic Commands

```bash
# Show help
./pp --help

# Show version  
./pp --version

# List accounts in a portfolio file
./pp --file my-portfolio.xml accounts list

# List transactions with filtering
./pp --file my-portfolio.xml transactions list --from 2024-01-01 --to 2024-12-31

# Get JSON output for scripting
./pp --file my-portfolio.xml --format json accounts list
```

### Command Structure

```
pp --file <portfolio.xml> [global-options] <command> [command-options]
```

**Global Options:**
- `--file, -f <file>` - Portfolio file path (required)
- `--format <format>` - Output format: table (default), json
- `--verbose, -v` - Verbose output
- `--backup` - Create backup before writing (for future write operations)
- `--dry-run` - Show what would be done without making changes

**Commands:**
- `accounts list` - List all accounts with balances
- `transactions list` - List transactions with optional filtering
  - `--account <name>` - Filter by account name
  - `--type <type>` - Filter by transaction type  
  - `--from <date>` - Start date (YYYY-MM-DD)
  - `--to <date>` - End date (YYYY-MM-DD)

## Output Formats

### Table Format (default)
Human-readable tabular output suitable for terminal viewing.

### JSON Format
Machine-readable output perfect for scripting and integration:

```bash
./pp --file portfolio.xml --format json accounts list | jq '.[] | select(.currency=="USD")'
```

## Environment Variables

- `PP_JVM_OPTS` - Additional JVM options (default: `-Xmx1G`)
- `PP_DEBUG` - Set to 'true' to enable debug output
- `JAVA_HOME` - Java installation directory (Java 17+ required)

## File Compatibility

- **Supported**: Unencrypted Portfolio Performance XML files
- **Not supported**: Encrypted portfolio files (will show clear error message)

## Technical Details

### Dependencies
- **picocli 4.7.5**: Command-line parsing and help generation
- **Jackson 2.17.0**: JSON serialization with Java 8+ time support
- **XStream 1.4.20**: XML parsing for Portfolio Performance files

### Error Handling
- Clear error messages for common issues (missing files, encryption, invalid arguments)
- Encryption detection with helpful guidance
- Build verification with instructions when fat jar is missing

### Integration Examples

**With jq for JSON processing:**
```bash
# Get accounts with balance > 1000
./pp --file portfolio.xml --format json accounts list | \
  jq '.[] | select(.balance_amount > 100000)'

# Export transactions to CSV
./pp --file portfolio.xml --format json transactions list | \
  jq -r '.[] | [.date, .type, .amount, .account] | @csv'
```

**With bash scripting:**
```bash
#!/bin/bash
# Monitor portfolio changes
for file in portfolios/*.xml; do
  echo "=== $(basename "$file") ==="
  ./pp --file "$file" accounts list
done
```

## Development

### Project Structure
```
name.abuchen.portfolio.cli/
├── src/main/java/name/abuchen/portfolio/cli/
│   ├── commands/           # CLI command classes
│   ├── model/             # Simplified domain model  
│   ├── services/          # Business logic services
│   └── util/              # Output formatting utilities
├── src/test/java/         # Unit tests
└── pom.xml               # Standalone Maven project
```

### Adding New Commands

1. Create command class in `commands/` package
2. Annotate with `@Command` from picocli
3. Add as subcommand to `PortfolioCLI`
4. Implement business logic in `services/` package

### Running Tests

```bash
mvn test
```

## Future Enhancements

The CLI foundation supports future extensions for:
- Transaction creation and editing
- Account management operations
- Data validation and integrity checks  
- CSV import/export functionality
- Bulk operations across multiple portfolio files
- Integration with external APIs and data sources

## Troubleshooting

**"CLI fat jar not found"**
- Run: `cd name.abuchen.portfolio.cli && mvn clean package -DskipTests`

**"Java 17+ required"**  
- Install Java 17+ and set `JAVA_HOME` environment variable

**"Encrypted portfolio files not supported"**
- Export your portfolio to unencrypted XML format from Portfolio Performance

**For detailed debugging:**
```bash
PP_DEBUG=true ./pp --file portfolio.xml accounts list
```