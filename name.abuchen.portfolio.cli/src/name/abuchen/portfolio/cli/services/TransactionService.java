package name.abuchen.portfolio.cli.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import name.abuchen.portfolio.model.Account;
import name.abuchen.portfolio.model.AccountTransaction;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.Portfolio;
import name.abuchen.portfolio.model.PortfolioTransaction;
import name.abuchen.portfolio.model.Transaction;

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
        for (Account account : client.getAccounts())
        {
            if (accountName != null && !account.getName().equalsIgnoreCase(accountName))
                continue;

            for (AccountTransaction transaction : account.getTransactions())
            {
                if (shouldIncludeTransaction(transaction, type, fromDate, toDate))
                {
                    Map<String, Object> txInfo = createTransactionMap(transaction, account.getName(), null);
                    transactions.add(txInfo);
                }
            }
        }

        // Get portfolio transactions
        for (Portfolio portfolio : client.getPortfolios())
        {
            for (PortfolioTransaction transaction : portfolio.getTransactions())
            {
                if (shouldIncludeTransaction(transaction, type, fromDate, toDate))
                {
                    Map<String, Object> txInfo = createTransactionMap(transaction, null, portfolio.getName());
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

    private boolean shouldIncludeTransaction(Transaction transaction, String type, 
                                           LocalDate fromDate, LocalDate toDate)
    {
        // Check date range
        if (fromDate != null && transaction.getDateTime().toLocalDate().isBefore(fromDate))
            return false;
        if (toDate != null && transaction.getDateTime().toLocalDate().isAfter(toDate))
            return false;

        // Check type filter
        if (type != null)
        {
            String txType = getTransactionTypeString(transaction);
            if (!txType.equalsIgnoreCase(type))
                return false;
        }

        return true;
    }

    private Map<String, Object> createTransactionMap(Transaction transaction, String accountName, String portfolioName)
    {
        Map<String, Object> txInfo = new HashMap<>();
        
        txInfo.put("date", transaction.getDateTime().toLocalDate());
        txInfo.put("type", getTransactionTypeString(transaction));
        txInfo.put("amount", transaction.getAmount());
        txInfo.put("currency", transaction.getCurrencyCode());
        txInfo.put("note", transaction.getNote() != null ? transaction.getNote() : "");

        if (accountName != null)
            txInfo.put("account", accountName);
        if (portfolioName != null)
            txInfo.put("portfolio", portfolioName);

        // Add security information for portfolio transactions
        if (transaction instanceof PortfolioTransaction portfolioTx && portfolioTx.getSecurity() != null)
        {
            txInfo.put("security", portfolioTx.getSecurity().getName());
            txInfo.put("shares", portfolioTx.getShares());
            txInfo.put("isin", portfolioTx.getSecurity().getIsin());
        }

        return txInfo;
    }

    private String getTransactionTypeString(Transaction transaction)
    {
        if (transaction instanceof AccountTransaction accountTx)
        {
            return switch (accountTx.getType())
            {
                case DEPOSIT -> "deposit";
                case REMOVAL -> "withdrawal";
                case DIVIDENDS -> "dividend";
                case INTEREST -> "interest";
                case FEES -> "fee";
                case TAXES -> "tax";
                case TAX_REFUND -> "tax_refund";
                case BUY -> "buy";
                case SELL -> "sell";
                case TRANSFER_IN -> "transfer_in";
                case TRANSFER_OUT -> "transfer_out";
            };
        }
        else if (transaction instanceof PortfolioTransaction portfolioTx)
        {
            return switch (portfolioTx.getType())
            {
                case BUY -> "buy";
                case SELL -> "sell";
                case TRANSFER_IN -> "transfer_in";
                case TRANSFER_OUT -> "transfer_out";
                case DELIVERY_INBOUND -> "delivery_in";
                case DELIVERY_OUTBOUND -> "delivery_out";
            };
        }
        
        return "unknown";
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