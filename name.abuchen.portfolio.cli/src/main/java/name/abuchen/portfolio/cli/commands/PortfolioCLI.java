package name.abuchen.portfolio.cli.commands;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import name.abuchen.portfolio.cli.services.AccountService;
import name.abuchen.portfolio.cli.services.PortfolioLoader;
import name.abuchen.portfolio.cli.services.TransactionService;
import name.abuchen.portfolio.cli.util.OutputFormatter;
import name.abuchen.portfolio.cli.model.Client;

/**
 * Main CLI entry point for Portfolio Performance.
 */
@Command(
    name = "pp",
    description = "Portfolio Performance CLI - manage and query investment portfolios",
    mixinStandardHelpOptions = true,
    version = "Portfolio Performance CLI 0.78.2",
    subcommands = {
        PortfolioCLI.AccountsCommand.class,
        PortfolioCLI.TransactionsCommand.class
    }
)
public class PortfolioCLI implements Callable<Integer>
{
    @Option(names = {"-f", "--file"}, required = true, description = "Portfolio file path (required)")
    private File portfolioFile;

    @Option(names = {"--format"}, description = "Output format: table (default), json")
    private OutputFormatter.Format format = OutputFormatter.Format.TABLE;

    @Option(names = {"--backup"}, description = "Create backup before writing")
    private boolean createBackup = false;

    @Option(names = {"--dry-run"}, description = "Show what would be done without making changes")
    private boolean dryRun = false;

    @Option(names = {"-v", "--verbose"}, description = "Verbose output")
    private boolean verbose = false;

    private PortfolioLoader portfolioLoader = new PortfolioLoader();
    private Client cachedClient;

    /**
     * Main entry point.
     */
    public static void main(String[] args)
    {
        int exitCode = new CommandLine(new PortfolioCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception
    {
        // Show help if no subcommand is specified
        CommandLine.usage(this, System.out);
        return 0;
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
            
            for (Client.Account account : cachedClient.getAccounts()) 
            {
                System.err.println("Account: " + account.getName() + " (UUID: " + account.getUuid() + 
                    ", transactions: " + (account.getTransactions() != null ? account.getTransactions().size() : "null") + ")");
            }
        }

        return cachedClient;
    }

    // Getters for subcommands to access parent options
    public OutputFormatter.Format getFormat() { return format; }
    public boolean isVerbose() { return verbose; }
    public boolean isDryRun() { return dryRun; }
    public boolean shouldCreateBackup() { return createBackup; }
    public File getPortfolioFile() { return portfolioFile; }

    /**
     * Accounts command
     */
    @Command(name = "accounts", description = "Manage accounts")
    static class AccountsCommand implements Callable<Integer>
    {
        @CommandLine.ParentCommand
        private PortfolioCLI parent;

        @Command(name = "list", description = "List all accounts")
        public Integer list() throws Exception
        {
            Client client = parent.loadClient();
            AccountService accountService = new AccountService(client);
            
            List<Map<String, Object>> accounts = accountService.listAccounts();
            
            if (accounts.isEmpty())
            {
                System.out.println("No accounts found.");
                return 0;
            }

            String output = switch (parent.getFormat())
            {
                case JSON -> OutputFormatter.formatAsJson(accounts);
                case TABLE -> OutputFormatter.formatAsTable(accounts, "name", "currency", "balance", "note");
            };
            
            System.out.println(output);
            return 0;
        }

        @Override
        public Integer call() throws Exception
        {
            // Show help if no subcommand is specified
            CommandLine.usage(this, System.out);
            return 0;
        }
    }

    /**
     * Transactions command
     */
    @Command(name = "transactions", description = "Manage transactions")
    static class TransactionsCommand implements Callable<Integer>
    {
        @CommandLine.ParentCommand
        private PortfolioCLI parent;

        @Command(name = "list", description = "List transactions")
        public Integer list(
            @Option(names = {"--account"}, description = "Filter by account name") String accountName,
            @Option(names = {"--type"}, description = "Filter by transaction type") String type,
            @Option(names = {"--from"}, description = "Start date (YYYY-MM-DD format)") String fromDate,
            @Option(names = {"--to"}, description = "End date (YYYY-MM-DD format)") String toDate
        ) throws Exception
        {
            LocalDate from = parseDate(fromDate, "from");
            LocalDate to = parseDate(toDate, "to");

            Client client = parent.loadClient();
            TransactionService transactionService = new TransactionService(client);
            
            List<Map<String, Object>> transactions = transactionService.listTransactions(
                accountName, type, from, to);
            
            if (transactions.isEmpty())
            {
                System.out.println("No transactions found matching the criteria.");
                return 0;
            }

            String output = switch (parent.getFormat())
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

        @Override
        public Integer call() throws Exception
        {
            // Show help if no subcommand is specified
            CommandLine.usage(this, System.out);
            return 0;
        }
    }
}