package name.abuchen.portfolio.cli.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.abuchen.portfolio.cli.model.Client;

/**
 * Service for working with accounts.
 */
public class AccountService
{
    private final Client client;

    public AccountService(Client client)
    {
        this.client = client;
    }

    /**
     * List all accounts with basic information.
     */
    public List<Map<String, Object>> listAccounts()
    {
        List<Map<String, Object>> accounts = new ArrayList<>();

        for (Client.Account account : client.getAccounts())
        {
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("name", account.getName());
            accountInfo.put("currency", account.getCurrencyCode());
            accountInfo.put("note", account.getNote() != null ? account.getNote() : "");

            // Calculate simple balance (sum of all transactions)
            long balance = calculateBalance(account);
            accountInfo.put("balance", formatAmount(balance, account.getCurrencyCode()));
            accountInfo.put("balance_amount", balance);

            accounts.add(accountInfo);
        }

        return accounts;
    }

    /**
     * Find account by name (case-insensitive).
     */
    public Client.Account findAccountByName(String name)
    {
        return client.getAccounts().stream()
                .filter(account -> account.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get account count.
     */
    public int getAccountCount()
    {
        return client.getAccounts().size();
    }

    private long calculateBalance(Client.Account account)
    {
        if (account.getTransactions() == null) 
        {
            return 0;
        }
        
        return account.getTransactions().stream()
                .mapToLong(transaction -> {
                    // Simple heuristic for transaction types
                    String type = transaction.getType();
                    if (type == null) return 0;
                    
                    // Income types
                    if (type.toLowerCase().contains("deposit") || 
                        type.toLowerCase().contains("dividend") || 
                        type.toLowerCase().contains("interest") ||
                        type.toLowerCase().contains("sell") ||
                        type.toLowerCase().contains("transfer_in"))
                    {
                        return transaction.getAmount();
                    }
                    // Expense types
                    else if (type.toLowerCase().contains("removal") || 
                             type.toLowerCase().contains("fee") || 
                             type.toLowerCase().contains("tax") ||
                             type.toLowerCase().contains("buy") ||
                             type.toLowerCase().contains("transfer_out"))
                    {
                        return -transaction.getAmount();
                    }
                    
                    return 0;
                })
                .sum();
    }

    private String formatAmount(long amount, String currency)
    {
        // Convert from cents to main currency unit
        double value = amount / 100.0;
        return String.format("%.2f %s", value, currency);
    }
}