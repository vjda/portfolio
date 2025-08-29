package name.abuchen.portfolio.cli.services;

import java.time.LocalDate;
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
        return account.getTransactions().stream()
                .mapToLong(Client.AccountTransaction::getAmount)
                .sum();
    }

    private String formatAmount(long amount, String currency)
    {
        // Convert from cents to main currency unit
        double value = amount / 100.0;
        return String.format("%.2f %s", value, currency);
    }
}