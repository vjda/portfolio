package name.abuchen.portfolio.cli.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.abuchen.portfolio.cli.model.Client;

/**
 * Service for working with transactions.
 */
public class TransactionService
{
    private final Client client;

    public TransactionService(Client client)
    {
        this.client = client;
    }

    /**
     * List transactions with optional filters.
     */
    public List<Map<String, Object>> listTransactions(String accountName, String type, 
                                                      LocalDate fromDate, LocalDate toDate)
    {
        List<Map<String, Object>> transactions = new ArrayList<>();

        // Get account transactions
        for (Client.Account account : client.getAccounts())
        {
            if (accountName != null && !account.getName().equalsIgnoreCase(accountName))
                continue;

            for (Client.AccountTransaction transaction : account.getTransactions())
            {
                if (shouldIncludeTransaction(transaction, type, fromDate, toDate))
                {
                    Map<String, Object> txInfo = createAccountTransactionMap(transaction, account.getName());
                    transactions.add(txInfo);
                }
            }
        }

        // Get portfolio transactions
        for (Client.Portfolio portfolio : client.getPortfolios())
        {
            for (Client.PortfolioTransaction transaction : portfolio.getTransactions())
            {
                if (shouldIncludeTransaction(transaction, type, fromDate, toDate))
                {
                    Map<String, Object> txInfo = createPortfolioTransactionMap(transaction, portfolio.getName());
                    transactions.add(txInfo);
                }
            }
        }

        // Sort by date (most recent first)
        transactions.sort((a, b) -> {
            LocalDate dateA = (LocalDate) a.get("date");
            LocalDate dateB = (LocalDate) b.get("date");
            return dateB.compareTo(dateA);
        });

        return transactions;
    }

    private boolean shouldIncludeTransaction(Client.AccountTransaction transaction, String type, 
                                           LocalDate fromDate, LocalDate toDate)
    {
        // Check date range
        if (fromDate != null && transaction.getDate().isBefore(fromDate))
            return false;
        if (toDate != null && transaction.getDate().isAfter(toDate))
            return false;

        // Check type filter
        if (type != null && transaction.getType() != null)
        {
            if (!transaction.getType().equalsIgnoreCase(type))
                return false;
        }

        return true;
    }

    private boolean shouldIncludeTransaction(Client.PortfolioTransaction transaction, String type, 
                                           LocalDate fromDate, LocalDate toDate)
    {
        // Check date range
        if (fromDate != null && transaction.getDate().isBefore(fromDate))
            return false;
        if (toDate != null && transaction.getDate().isAfter(toDate))
            return false;

        // Check type filter
        if (type != null && transaction.getType() != null)
        {
            if (!transaction.getType().equalsIgnoreCase(type))
                return false;
        }

        return true;
    }

    private Map<String, Object> createAccountTransactionMap(Client.AccountTransaction transaction, String accountName)
    {
        Map<String, Object> txInfo = new HashMap<>();
        
        txInfo.put("date", transaction.getDate());
        txInfo.put("type", transaction.getType() != null ? transaction.getType() : "unknown");
        txInfo.put("amount", formatAmount(transaction.getAmount(), transaction.getCurrencyCode()));
        txInfo.put("currency", transaction.getCurrencyCode());
        txInfo.put("note", transaction.getNote() != null ? transaction.getNote() : "");
        txInfo.put("account", accountName);
        txInfo.put("portfolio", "");
        txInfo.put("security", transaction.getSecurity() != null ? transaction.getSecurity() : "");

        return txInfo;
    }

    private Map<String, Object> createPortfolioTransactionMap(Client.PortfolioTransaction transaction, String portfolioName)
    {
        Map<String, Object> txInfo = new HashMap<>();
        
        txInfo.put("date", transaction.getDate());
        txInfo.put("type", transaction.getType() != null ? transaction.getType() : "unknown");
        txInfo.put("amount", formatAmount(transaction.getAmount(), transaction.getCurrencyCode()));
        txInfo.put("currency", transaction.getCurrencyCode());
        txInfo.put("note", transaction.getNote() != null ? transaction.getNote() : "");
        txInfo.put("account", "");
        txInfo.put("portfolio", portfolioName);
        txInfo.put("security", transaction.getSecurity() != null ? transaction.getSecurity() : "");

        // Add shares for portfolio transactions
        if (transaction.getShares() > 0)
        {
            txInfo.put("shares", transaction.getShares());
        }

        return txInfo;
    }

    private String formatAmount(long amount, String currency)
    {
        // Convert from cents to main currency unit
        double value = amount / 100.0;
        return String.format("%.2f %s", value, currency);
    }

    /**
     * Get total transaction count.
     */
    public int getTransactionCount()
    {
        return client.getAccounts().stream()
                .mapToInt(account -> account.getTransactions().size())
                .sum() +
               client.getPortfolios().stream()
                .mapToInt(portfolio -> portfolio.getTransactions().size())
                .sum();
    }
}