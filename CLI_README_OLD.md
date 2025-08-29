# Portfolio Performance CLI

This directory contains a command-line interface (CLI) for Portfolio Performance that allows you to read and query portfolio data without the graphical interface.

## Prerequisites

- Java 21 or higher
- Maven (for building)

## Setup

1. **Build the project:**
   ```bash
   mvn -f portfolio-app/pom.xml clean compile -Plocal-dev
   ```

2. **Make the script executable (if needed):**
   ```bash
   chmod +x pp
   ```

## Usage

The CLI provides a simple shell script `pp` that launches the Java application:

```bash
# List all accounts
./pp --file my-portfolio.xml accounts list

# List transactions with JSON output
./pp --file my-portfolio.xml --format json transactions list

# List transactions for a specific account
./pp --file my-portfolio.xml transactions list --account "My Savings"

# List transactions with date range
./pp --file my-portfolio.xml transactions list --from 2024-01-01 --to 2024-12-31

# Show help
./pp --help
```

## Command Reference

### Global Options
- `--file, -f <file>` - Portfolio file path (required)
- `--format <format>` - Output format: `table` (default), `json`
- `--verbose, -v` - Verbose output
- `--help` - Show help

### Commands

#### Accounts
```bash
./pp --file portfolio.xml accounts list
```
Lists all accounts with name, currency, current balance, and notes.

#### Transactions
```bash
./pp --file portfolio.xml transactions list [options]
```
Lists transactions with optional filtering:
- `--account <name>` - Filter by account name
- `--type <type>` - Filter by transaction type (buy, sell, dividend, deposit, withdrawal, etc.)
- `--from <date>` - Start date (YYYY-MM-DD format)
- `--to <date>` - End date (YYYY-MM-DD format)

## Environment Variables

- `PP_JVM_OPTS` - Additional JVM options (default: `-Xmx1G --enable-native-access=ALL-UNNAMED`)
- `PP_DEBUG` - Set to `true` to enable debug output
- `JAVA_HOME` - Java installation directory (Java 21+ required)

## Examples

```bash
# Basic usage
./pp --file ~/Documents/my-portfolio.xml accounts list

# JSON output for scripting
./pp --file ~/Documents/my-portfolio.xml --format json transactions list > transactions.json

# Filter recent buy transactions
./pp --file ~/Documents/my-portfolio.xml transactions list --type buy --from 2024-01-01

# Debug mode
PP_DEBUG=true ./pp --file ~/Documents/my-portfolio.xml accounts list

# Custom JVM options
PP_JVM_OPTS="-Xmx2G -Duser.timezone=UTC" ./pp --file ~/Documents/my-portfolio.xml accounts list
```

## Supported File Formats

- **Unencrypted XML files**: Standard Portfolio Performance XML format
- **Encrypted files**: Not supported (will show error with clear message)

## Troubleshooting

1. **"Project not built" error**: Run the Maven build command shown above
2. **Java version error**: Install Java 21+ and set `JAVA_HOME` appropriately
3. **File not found**: Ensure the portfolio file path is correct and accessible
4. **Encrypted file error**: Export your portfolio to unencrypted XML format first

## Integration with Other Tools

The JSON output format makes it easy to integrate with other tools:

```bash
# Extract account names with jq
./pp --file portfolio.xml --format json accounts list | jq -r '.[].name'

# Count transactions by type
./pp --file portfolio.xml --format json transactions list | jq 'group_by(.type) | map({type: .[0].type, count: length})'

# Export to CSV (requires jq and csvkit)
./pp --file portfolio.xml --format json transactions list | jq -r '(.[0] | keys_unsorted) as $keys | $keys, (.[] | [.[$keys[]]) | @csv'
```