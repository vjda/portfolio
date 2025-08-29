package name.abuchen.portfolio.cli.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import name.abuchen.portfolio.cli.util.XStreamConfigurationUtil;
import name.abuchen.portfolio.cli.model.Client;

/**
 * Service for loading Portfolio Performance files using XStream.
 */
public class PortfolioLoader
{
    private final XStream xstream;

    public PortfolioLoader()
    {
        xstream = XStreamConfigurationUtil.createConfiguredXStream();
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