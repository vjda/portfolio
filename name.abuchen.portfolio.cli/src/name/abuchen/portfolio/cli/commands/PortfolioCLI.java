package name.abuchen.portfolio.cli.commands;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import name.abuchen.portfolio.cli.services.AccountService;
import name.abuchen.portfolio.cli.services.PortfolioLoader;
import name.abuchen.portfolio.cli.services.TransactionService;
import name.abuchen.portfolio.cli.util.OutputFormatter;
import name.abuchen.portfolio.model.Client;

/**
 * Main CLI entry point for Portfolio Performance.
 */
public class PortfolioCLI
{
    private File portfolioFile;
    private OutputFormatter.Format format = OutputFormatter.Format.TABLE;
    private boolean createBackup = false;
    private boolean dryRun = false;
    private boolean verbose = false;

    private PortfolioLoader portfolioLoader = new PortfolioLoader();
    private Client cachedClient;

    /**
     * Main entry point.
     */
    public static void main(String[] args)
    {
        try
        {
            PortfolioCLI cli = new PortfolioCLI();
            int exitCode = cli.run(args);
            System.exit(exitCode);
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    public int run(String[] args)
    {
        try
        {
            if (args.length == 0)
            {
                showUsage();
                return 1;
            }

            // Parse global options
            int commandStart = parseGlobalOptions(args);
            if (commandStart >= args.length)
            {
                showUsage();
                return 1;
            }

            // Validate required options
            if (portfolioFile == null)
            {
                System.err.println("Error: --file option is required");
                showUsage();
                return 1;
            }

            // Parse and execute command
            String command = args[commandStart];
            String[] commandArgs = Arrays.copyOfRange(args, commandStart + 1, args.length);

            return switch (command)
            {
                case "accounts" -> executeAccountsCommand(commandArgs);
                case "transactions" -> executeTransactionsCommand(commandArgs);
                case "help", "--help", "-h" -> {
                    showUsage();
                    yield 0;
                }
                default -> {
                    System.err.println("Unknown command: " + command);
                    showUsage();
                    yield 1;
                }
            };
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            if (verbose)
            {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private int parseGlobalOptions(String[] args)
    {
        int i = 0;
        while (i < args.length)
        {
            String arg = args[i];
            if (arg.equals("--file") || arg.equals("-f"))
            {
                if (i + 1 >= args.length)
                {
                    throw new IllegalArgumentException("--file requires a value");
                }
                portfolioFile = new File(args[++i]);
            }
            else if (arg.equals("--format"))
            {
                if (i + 1 >= args.length)
                {
                    throw new IllegalArgumentException("--format requires a value");
                }
                String formatStr = args[++i];
                format = "json".equalsIgnoreCase(formatStr) ? OutputFormatter.Format.JSON : OutputFormatter.Format.TABLE;
            }
            else if (arg.equals("--backup"))
            {
                createBackup = true;
            }
            else if (arg.equals("--dry-run"))
            {
                dryRun = true;
            }
            else if (arg.equals("--verbose") || arg.equals("-v"))
            {
                verbose = true;
            }
            else if (arg.startsWith("-"))
            {
                throw new IllegalArgumentException("Unknown option: " + arg);
            }
            else
            {
                // First non-option argument is the command
                return i;
            }
            i++;
        }
        return i;
    }

    private int executeAccountsCommand(String[] args) throws Exception
    {
        if (args.length == 0 || !args[0].equals("list"))
        {
            System.err.println("Usage: accounts list");
            return 1;
        }

        Client client = loadClient();
        AccountService accountService = new AccountService(client);
        
        List<Map<String, Object>> accounts = accountService.listAccounts();
        
        if (accounts.isEmpty())
        {
            System.out.println("No accounts found.");
            return 0;
        }

        String output = switch (format)
        {
            case JSON -> OutputFormatter.formatAsJson(accounts);
            case TABLE -> OutputFormatter.formatAsTable(accounts, "name", "currency", "balance", "note");
        };
        
        System.out.println(output);
        return 0;
    }

    private int executeTransactionsCommand(String[] args) throws Exception
    {
        if (args.length == 0 || !args[0].equals("list"))
        {
            System.err.println("Usage: transactions list [--account <name>] [--type <type>] [--from <date>] [--to <date>]");
            return 1;
        }

        // Parse transaction command options
        String accountName = null;
        String type = null;
        LocalDate fromDate = null;
        LocalDate toDate = null;

        for (int i = 1; i < args.length; i++)
        {
            String arg = args[i];
            if (arg.equals("--account") && i + 1 < args.length)
            {
                accountName = args[++i];
            }
            else if (arg.equals("--type") && i + 1 < args.length)
            {
                type = args[++i];
            }
            else if (arg.equals("--from") && i + 1 < args.length)
            {
                fromDate = parseDate(args[++i], "from");
            }
            else if (arg.equals("--to") && i + 1 < args.length)
            {
                toDate = parseDate(args[++i], "to");
            }
        }

        Client client = loadClient();
        TransactionService transactionService = new TransactionService(client);
        
        List<Map<String, Object>> transactions = transactionService.listTransactions(
            accountName, type, fromDate, toDate);
        
        if (transactions.isEmpty())
        {
            System.out.println("No transactions found matching the criteria.");
            return 0;
        }

        String output = switch (format)
        {
            case JSON -> OutputFormatter.formatAsJson(transactions);
            case TABLE -> OutputFormatter.formatAsTable(transactions, 
                "date", "type", "amount", "currency", "account", "portfolio", "security", "note");
        };
        
        System.out.println(output);
        return 0;
    }

    private LocalDate parseDate(String dateStr, String fieldName)
    {
        if (dateStr == null || dateStr.trim().isEmpty())
            return null;

        try
        {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        catch (DateTimeParseException e)
        {
            throw new IllegalArgumentException("Invalid " + fieldName + " date format. Use YYYY-MM-DD: " + dateStr);
        }
    }

    /**
     * Load the client, with caching.
     */
    public Client loadClient() throws Exception
    {
        if (cachedClient != null)
            return cachedClient;

        if (!portfolioFile.exists())
        {
            throw new Exception("Portfolio file does not exist: " + portfolioFile.getAbsolutePath());
        }

        if (portfolioLoader.isEncrypted(portfolioFile))
        {
            throw new Exception("Encrypted portfolio files are not supported by the CLI. " +
                    "Please export to unencrypted XML format first.");
        }

        if (verbose)
        {
            System.err.println("Loading portfolio from: " + portfolioFile.getAbsolutePath());
        }

        cachedClient = portfolioLoader.load(portfolioFile);
        
        if (verbose)
        {
            System.err.println("Portfolio loaded successfully. " +
                    "Accounts: " + cachedClient.getAccounts().size() + 
                    ", Portfolios: " + cachedClient.getPortfolios().size() +
                    ", Securities: " + cachedClient.getSecurities().size());
        }

        return cachedClient;
    }

    private void showUsage()
    {
        System.out.println("Portfolio Performance CLI");
        System.out.println();
        System.out.println("Usage: pp --file <portfolio.xml> [options] <command> [command-options]");
        System.out.println();
        System.out.println("Global Options:");
        System.out.println("  --file, -f <file>     Portfolio file path (required)");
        System.out.println("  --format <format>     Output format: table (default), json");
        System.out.println("  --backup              Create backup before writing");
        System.out.println("  --dry-run             Show what would be done without making changes");
        System.out.println("  --verbose, -v         Verbose output");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  accounts list         List all accounts");
        System.out.println("  transactions list     List transactions");
        System.out.println("                        [--account <name>] [--type <type>] [--from <date>] [--to <date>]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  pp --file portfolio.xml accounts list");
        System.out.println("  pp --file portfolio.xml --format json transactions list");
        System.out.println("  pp --file portfolio.xml transactions list --from 2024-01-01 --to 2024-12-31");
    }

    public OutputFormatter.Format getFormat()
    {
        return format;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean shouldCreateBackup()
    {
        return createBackup;
    }

    public File getPortfolioFile()
    {
        return portfolioFile;
    }
}