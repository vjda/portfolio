package name.abuchen.portfolio.cli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified Portfolio Performance client model for CLI use.
 * This is a minimal representation that can parse existing portfolio files
 * without requiring the full core module dependencies.
 */
public class Client
{
    private String version;
    private String baseCurrency = "EUR";
    private List<Account> accounts = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();
    private List<Security> securities = new ArrayList<>();

    // Getters and setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    
    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }
    
    public List<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(List<Portfolio> portfolios) { this.portfolios = portfolios; }
    
    public List<Security> getSecurities() { return securities; }
    public void setSecurities(List<Security> securities) { this.securities = securities; }

    /**
     * Simplified Account model
     */
    public static class Account
    {
        private String uuid;
        private String name;
        private String note;
        private String currencyCode = "EUR";
        private List<AccountTransaction> transactions = new ArrayList<>();

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        
        public List<AccountTransaction> getTransactions() { return transactions; }
        public void setTransactions(List<AccountTransaction> transactions) { this.transactions = transactions; }
    }

    /**
     * Simplified Portfolio model
     */
    public static class Portfolio
    {
        private String uuid;
        private String name;
        private String note;
        private String referenceAccount;
        private List<PortfolioTransaction> transactions = new ArrayList<>();

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        
        public String getReferenceAccount() { return referenceAccount; }
        public void setReferenceAccount(String referenceAccount) { this.referenceAccount = referenceAccount; }
        
        public List<PortfolioTransaction> getTransactions() { return transactions; }
        public void setTransactions(List<PortfolioTransaction> transactions) { this.transactions = transactions; }
    }

    /**
     * Simplified Security model
     */
    public static class Security
    {
        private String uuid;
        private String name;
        private String isin;
        private String symbol;
        private String currencyCode = "EUR";

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getIsin() { return isin; }
        public void setIsin(String isin) { this.isin = isin; }
        
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    /**
     * Simplified Account Transaction model
     */
    public static class AccountTransaction
    {
        private LocalDate date;
        private String type;
        private long amount; // in cents
        private String currencyCode = "EUR";
        private String note;
        private String security;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public long getAmount() { return amount; }
        public void setAmount(long amount) { this.amount = amount; }
        
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        
        public String getSecurity() { return security; }
        public void setSecurity(String security) { this.security = security; }
    }

    /**
     * Simplified Portfolio Transaction model
     */
    public static class PortfolioTransaction
    {
        private LocalDate date;
        private String type;
        private long amount; // in cents
        private String currencyCode = "EUR";
        private long shares; // scaled by 1000000
        private String note;
        private String security;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public long getAmount() { return amount; }
        public void setAmount(long amount) { this.amount = amount; }
        
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        
        public long getShares() { return shares; }
        public void setShares(long shares) { this.shares = shares; }
        
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
        
        public String getSecurity() { return security; }
        public void setSecurity(String security) { this.security = security; }
    }
}