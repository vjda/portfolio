package name.abuchen.portfolio.cli.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.abuchen.portfolio.model.Account;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.money.CurrencyConverter;
import name.abuchen.portfolio.money.CurrencyConverterImpl;
import name.abuchen.portfolio.money.ExchangeRateProviderFactory;
import name.abuchen.portfolio.snapshot.AccountSnapshot;

/**
 * Service for working with accounts.
 */
public class AccountService
{
    private final Client client;
    private final CurrencyConverter converter;

    public AccountService(Client client)
    {
        this.client = client;
        // Use the same currency converter setup as the main application
        this.converter = new CurrencyConverterImpl(ExchangeRateProviderFactory.getExchangeRateProvider(),
                client.getBaseCurrency());
    }

    /**
     * List all accounts with basic information.
     */
    public List<Map<String, Object>> listAccounts()
    {
        List<Map<String, Object>> accounts = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (Account account : client.getAccounts())
        {
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("name", account.getName());
            accountInfo.put("currency", account.getCurrencyCode());
            accountInfo.put("note", account.getNote() != null ? account.getNote() : "");

            // Calculate current balance using snapshot
            try
            {
                AccountSnapshot snapshot = AccountSnapshot.create(account, converter, now);
                accountInfo.put("balance", snapshot.getFunds().toString());
                accountInfo.put("balance_amount", snapshot.getFunds().getAmount());
            }
            catch (Exception e)
            {
                accountInfo.put("balance", "Error calculating balance");
                accountInfo.put("balance_amount", 0L);
            }

            accounts.add(accountInfo);
        }

        return accounts;
    }

    /**
     * Find account by name (case-insensitive).
     */
    public Account findAccountByName(String name)
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
}