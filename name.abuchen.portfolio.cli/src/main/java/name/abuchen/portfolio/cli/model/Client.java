package name.abuchen.portfolio.cli.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple portfolio client model for CLI use.
 */
public class Client
{
    private String version;
    private List<Account> accounts = new ArrayList<>();
    private List<Portfolio> portfolios = new ArrayList<>();
    private List<Security> securities = new ArrayList<>();

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public List<Account> getAccounts()
    {
        return accounts;
    }

    public void setAccounts(List<Account> accounts)
    {
        this.accounts = accounts;
    }

    public List<Portfolio> getPortfolios()
    {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios)
    {
        this.portfolios = portfolios;
    }

    public List<Security> getSecurities()
    {
        return securities;
    }

    public void setSecurities(List<Security> securities)
    {
        this.securities = securities;
    }

    /**
     * Simple account model.
     */
    public static class Account
    {
        private String uuid;
        private String name;
        private String currencyCode;
        private String note;
        private List<AccountTransaction> transactions = new ArrayList<>();

        public String getUuid()
        {
            return uuid;
        }

        public void setUuid(String uuid)
        {
            this.uuid = uuid;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getCurrencyCode()
        {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode)
        {
            this.currencyCode = currencyCode;
        }

        public String getNote()
        {
            return note;
        }

        public void setNote(String note)
        {
            this.note = note;
        }

        public List<AccountTransaction> getTransactions()
        {
            return transactions;
        }

        public void setTransactions(List<AccountTransaction> transactions)
        {
            this.transactions = transactions;
        }
    }

    /**
     * Simple portfolio model.
     */
    public static class Portfolio
    {
        private String uuid;
        private String name;
        private String referenceAccount;
        private List<PortfolioTransaction> transactions = new ArrayList<>();

        public String getUuid()
        {
            return uuid;
        }

        public void setUuid(String uuid)
        {
            this.uuid = uuid;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getReferenceAccount()
        {
            return referenceAccount;
        }

        public void setReferenceAccount(String referenceAccount)
        {
            this.referenceAccount = referenceAccount;
        }

        public List<PortfolioTransaction> getTransactions()
        {
            return transactions;
        }

        public void setTransactions(List<PortfolioTransaction> transactions)
        {
            this.transactions = transactions;
        }
    }

    /**
     * Simple security model.
     */
    public static class Security
    {
        private String uuid;
        private String name;
        private String isin;
        private String wkn;
        private String tickerSymbol;
        private String currencyCode;

        public String getUuid()
        {
            return uuid;
        }

        public void setUuid(String uuid)
        {
            this.uuid = uuid;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getIsin()
        {
            return isin;
        }

        public void setIsin(String isin)
        {
            this.isin = isin;
        }

        public String getWkn()
        {
            return wkn;
        }

        public void setWkn(String wkn)
        {
            this.wkn = wkn;
        }

        public String getTickerSymbol()
        {
            return tickerSymbol;
        }

        public void setTickerSymbol(String tickerSymbol)
        {
            this.tickerSymbol = tickerSymbol;
        }

        public String getCurrencyCode()
        {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode)
        {
            this.currencyCode = currencyCode;
        }
    }

    /**
     * Simple account transaction model.
     */
    public static class AccountTransaction
    {
        private LocalDate date;
        private String type;
        private String currencyCode;
        private long amount;
        private String note;
        private String security;

        public LocalDate getDate()
        {
            return date;
        }

        public void setDate(LocalDate date)
        {
            this.date = date;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getCurrencyCode()
        {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode)
        {
            this.currencyCode = currencyCode;
        }

        public long getAmount()
        {
            return amount;
        }

        public void setAmount(long amount)
        {
            this.amount = amount;
        }

        public String getNote()
        {
            return note;
        }

        public void setNote(String note)
        {
            this.note = note;
        }

        public String getSecurity()
        {
            return security;
        }

        public void setSecurity(String security)
        {
            this.security = security;
        }
    }

    /**
     * Simple portfolio transaction model.
     */
    public static class PortfolioTransaction
    {
        private LocalDate date;
        private String type;
        private String currencyCode;
        private long amount;
        private long shares;
        private String note;
        private String security;

        public LocalDate getDate()
        {
            return date;
        }

        public void setDate(LocalDate date)
        {
            this.date = date;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getCurrencyCode()
        {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode)
        {
            this.currencyCode = currencyCode;
        }

        public long getAmount()
        {
            return amount;
        }

        public void setAmount(long amount)
        {
            this.amount = amount;
        }

        public long getShares()
        {
            return shares;
        }

        public void setShares(long shares)
        {
            this.shares = shares;
        }

        public String getNote()
        {
            return note;
        }

        public void setNote(String note)
        {
            this.note = note;
        }

        public String getSecurity()
        {
            return security;
        }

        public void setSecurity(String security)
        {
            this.security = security;
        }
    }
}