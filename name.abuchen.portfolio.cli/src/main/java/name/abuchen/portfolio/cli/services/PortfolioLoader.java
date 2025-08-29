package name.abuchen.portfolio.cli.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import name.abuchen.portfolio.cli.model.Client;

/**
 * Service for loading Portfolio Performance files using XStream.
 */
public class PortfolioLoader
{
    private final XStream xstream;

    public PortfolioLoader()
    {
        xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        configureXStream();
    }

    private void configureXStream()
    {
        // Configure aliases for the main classes
        xstream.alias("client", Client.class);
        xstream.alias("account", Client.Account.class);
        xstream.alias("portfolio", Client.Portfolio.class);
        xstream.alias("security", Client.Security.class);
        xstream.alias("account-transaction", Client.AccountTransaction.class);
        xstream.alias("portfolio-transaction", Client.PortfolioTransaction.class);

        // Configure implicit collections - these are the collection names in the XML
        xstream.addImplicitCollection(Client.class, "accounts", "account", Client.Account.class);
        xstream.addImplicitCollection(Client.class, "portfolios", "portfolio", Client.Portfolio.class);
        xstream.addImplicitCollection(Client.class, "securities", "security", Client.Security.class);
        xstream.addImplicitCollection(Client.Account.class, "transactions", "account-transaction", Client.AccountTransaction.class);
        xstream.addImplicitCollection(Client.Portfolio.class, "transactions", "portfolio-transaction", Client.PortfolioTransaction.class);
    }

    /**
     * Load a Client from the specified file.
     */
    public Client load(File file) throws IOException
    {
        if (!file.exists())
            throw new IOException("Portfolio file does not exist: " + file.getAbsolutePath());

        if (!file.canRead())
            throw new IOException("Cannot read portfolio file: " + file.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(file))
        {
            Object result = xstream.fromXML(fis);
            if (result instanceof Client)
            {
                return (Client) result;
            }
            else
            {
                throw new IOException("Invalid portfolio file format - not a Portfolio Performance XML file");
            }
        }
        catch (Exception e)
        {
            throw new IOException("Failed to load portfolio file: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a file is encrypted by looking for the encryption marker.
     */
    public boolean isEncrypted(File file) throws IOException
    {
        if (!file.exists() || !file.canRead())
            return false;

        try (FileInputStream fis = new FileInputStream(file))
        {
            byte[] buffer = new byte[100];
            int bytesRead = fis.read(buffer);
            if (bytesRead > 0)
            {
                String content = new String(buffer, 0, bytesRead);
                // Check for common encryption markers or if it doesn't start with XML
                return !content.trim().startsWith("<?xml") || content.contains("encrypted");
            }
        }
        catch (Exception e)
        {
            // If we can't read it, assume it might be encrypted
            return true;
        }
        
        return false;
    }
}