package name.abuchen.portfolio.cli.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.ClientFactory;
import name.abuchen.portfolio.model.SaveFlag;

/**
 * Service for loading and saving Portfolio Performance files using the same
 * XStream configuration as the core application.
 */
public class PortfolioLoader
{
    /**
     * Load a Client from the specified file.
     */
    public Client load(File file) throws IOException
    {
        if (!file.exists())
            throw new IOException("Portfolio file does not exist: " + file.getAbsolutePath());

        if (!file.canRead())
            throw new IOException("Cannot read portfolio file: " + file.getAbsolutePath());

        return ClientFactory.load(file);
    }

    /**
     * Save a Client to the specified file with backup.
     */
    public void save(Client client, File file, boolean createBackup) throws IOException
    {
        if (createBackup && file.exists())
        {
            createBackupFile(file);
        }

        // Use the same save flags as the original application
        Set<SaveFlag> flags = EnumSet.of(SaveFlag.XML);
        ClientFactory.save(client, file, null, flags);
    }

    /**
     * Check if the file is encrypted (not supported by CLI).
     */
    public boolean isEncrypted(File file) throws IOException
    {
        if (!file.exists())
            return false;

        // Simple check - Portfolio encrypted files start with specific signature
        try (FileInputStream fis = new FileInputStream(file))
        {
            byte[] signature = new byte[8];
            int bytesRead = fis.read(signature);
            if (bytesRead == 8)
            {
                String sig = new String(signature);
                return "PORTFOLIO".equals(sig) || "PORTF".equals(sig.substring(0, 5));
            }
        }
        return false;
    }

    /**
     * Try to acquire an exclusive lock on the file to prevent concurrent access.
     */
    public FileLock tryLockFile(File file) throws IOException
    {
        FileChannel channel = new FileOutputStream(file, true).getChannel();
        return channel.tryLock();
    }

    /**
     * Create a backup copy of the file with timestamp.
     */
    private void createBackupFile(File originalFile) throws IOException
    {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String backupName = originalFile.getName() + ".backup." + timestamp;
        File backupFile = new File(originalFile.getParent(), backupName);

        try (FileInputStream src = new FileInputStream(originalFile);
             FileOutputStream dst = new FileOutputStream(backupFile))
        {
            src.getChannel().transferTo(0, src.getChannel().size(), dst.getChannel());
        }

        System.out.println("Created backup: " + backupFile.getAbsolutePath());
    }
}