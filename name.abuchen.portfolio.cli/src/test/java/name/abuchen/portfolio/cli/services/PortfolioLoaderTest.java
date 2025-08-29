package name.abuchen.portfolio.cli.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import name.abuchen.portfolio.cli.model.Client;

class PortfolioLoaderTest
{
    private PortfolioLoader loader;
    private File testFile;

    @BeforeEach
    void setUp()
    {
        loader = new PortfolioLoader();
        testFile = new File("../test-portfolio.xml");
    }

    @Test
    void testLoadPortfolioFile() throws IOException
    {
        // Skip test if test file doesn't exist
        if (!testFile.exists()) {
            return;
        }

        Client client = loader.load(testFile);
        
        assertNotNull(client);
        assertEquals(2, client.getAccounts().size());
        assertEquals(1, client.getPortfolios().size());
        assertEquals(1, client.getSecurities().size());
        
        // Test first account
        Client.Account account1 = client.getAccounts().get(0);
        assertEquals("Test Checking", account1.getName());
        assertEquals("USD", account1.getCurrencyCode());
        assertEquals(2, account1.getTransactions().size());
        
        // Test second account
        Client.Account account2 = client.getAccounts().get(1);
        assertEquals("Test Savings", account2.getName());
        assertEquals("EUR", account2.getCurrencyCode());
        assertEquals(1, account2.getTransactions().size());
    }

    @Test
    void testAccountService() throws IOException
    {
        // Skip test if test file doesn't exist
        if (!testFile.exists()) {
            return;
        }

        Client client = loader.load(testFile);
        AccountService accountService = new AccountService(client);
        
        List<Map<String, Object>> accounts = accountService.listAccounts();
        assertEquals(2, accounts.size());
        
        Map<String, Object> account1 = accounts.get(0);
        assertEquals("Test Checking", account1.get("name"));
        assertEquals("USD", account1.get("currency"));
        assertEquals("1000.00 USD", account1.get("balance"));
        
        Map<String, Object> account2 = accounts.get(1);
        assertEquals("Test Savings", account2.get("name"));
        assertEquals("EUR", account2.get("currency"));
        assertEquals("500.00 EUR", account2.get("balance"));
    }

    @Test
    void testNonExistentFile()
    {
        File nonExistent = new File("non-existent.xml");
        
        IOException exception = assertThrows(IOException.class, () -> {
            loader.load(nonExistent);
        });
        
        assertTrue(exception.getMessage().contains("does not exist"));
    }
}