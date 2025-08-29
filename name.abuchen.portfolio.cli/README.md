# Portfolio Performance CLI

A command-line interface for Portfolio Performance that allows you to read and manipulate portfolio files without the graphical user interface.

## Features

- **List accounts** - Display all accounts with name, currency, and current balance
- **List transactions** - Display transactions with optional filtering by account, type, and date range
- **JSON and table output** - Choose between human-readable table format or JSON for programmatic use
- **File format compatibility** - Uses the same XStream persistence layer as the main application
- **Safe file handling** - Supports backup creation and dry-run mode

## Usage

### Basic Syntax

```bash
pp --file <portfolio.xml> [global-options] <command> [command-options]
```

### Global Options

- `--file, -f <file>` - Portfolio file path (required)
- `--format <format>` - Output format: `table` (default) or `json`
- `--backup` - Create backup before writing (for future write operations)
- `--dry-run` - Show what would be done without making changes (for future write operations)
- `--verbose, -v` - Verbose output

### Commands

#### List Accounts

List all accounts with their basic information:

```bash
pp --file portfolio.xml accounts list
```

**Output columns:** Name, Currency, Balance, Note

#### List Transactions

List transactions with optional filtering:

```bash
pp --file portfolio.xml transactions list [options]
```

**Options:**
- `--account <name>` - Filter by account name
- `--type <type>` - Filter by transaction type (buy, sell, dividend, deposit, withdrawal, etc.)
- `--from <date>` - Start date (YYYY-MM-DD format)
- `--to <date>` - End date (YYYY-MM-DD format)

**Output columns:** Date, Type, Amount, Currency, Account, Portfolio, Security, Note

### Examples

```bash
# List all accounts in table format
pp --file my-portfolio.xml accounts list

# List all accounts in JSON format
pp --file my-portfolio.xml --format json accounts list

# List all transactions
pp --file my-portfolio.xml transactions list

# List transactions for a specific account
pp --file my-portfolio.xml transactions list --account "My Savings"

# List buy transactions from this year
pp --file my-portfolio.xml transactions list --type buy --from 2024-01-01

# List transactions in date range with JSON output
pp --file my-portfolio.xml --format json transactions list --from 2024-01-01 --to 2024-12-31
```

## Requirements

- Java 21 or higher
- Portfolio Performance portfolio file in unencrypted XML format
- The CLI does not support encrypted portfolio files

## File Format Notes

- The CLI reads the same XML format used by Portfolio Performance
- Both classic XML format and XML with ID references are supported
- Encrypted files are not supported - export to unencrypted XML first if needed
- The CLI uses the same XStream configuration as the main application for compatibility

## Architecture

The CLI module (`name.abuchen.portfolio.cli`) is built as an Eclipse OSGi plugin that:

- Depends on the core Portfolio Performance module (`name.abuchen.portfolio`)
- Reuses the existing domain model and persistence layer
- Provides a headless interface without any UI dependencies
- Follows the same modular architecture as other Portfolio Performance components

### Key Components

- **PortfolioCLI** - Main entry point and argument parsing
- **PortfolioLoader** - Service for loading/saving portfolio files using XStream
- **AccountService** - Operations on accounts and account data
- **TransactionService** - Operations on transactions with filtering
- **OutputFormatter** - Formatting output as tables or JSON

## Development

### Building

The CLI module is included in the main Portfolio Performance build:

```bash
mvn -f portfolio-app/pom.xml clean compile -Plocal-dev
```

### Testing

Basic functionality can be tested with:

```bash
# Create a sample portfolio file
echo '<?xml version="1.0" encoding="UTF-8"?>
<client version="73">
  <baseCurrency>EUR</baseCurrency>
  <accounts>
    <account>
      <name>Test Account</name>
      <currencyCode>EUR</currencyCode>
      <transactions/>
    </account>
  </accounts>
  <portfolios/>
  <securities/>
</client>' > sample.xml

# Test the CLI
pp --file sample.xml accounts list
```

## Future Enhancements

The CLI currently supports read-only operations. Future versions could include:

- **Transaction creation** - Add new buy/sell/dividend transactions
- **Transaction editing** - Modify existing transactions
- **Transaction deletion** - Remove transactions
- **Account management** - Create/edit accounts
- **Data validation** - Validate portfolio file integrity
- **Import/Export** - CSV import/export functionality
- **Bulk operations** - Batch processing of multiple files

## Error Handling

The CLI provides clear error messages for common issues:

- Missing or inaccessible portfolio files
- Encrypted files (not supported)
- Invalid date formats
- Missing required arguments
- Network connectivity issues (when needed for currency conversion)

Use `--verbose` flag for detailed error information including stack traces.