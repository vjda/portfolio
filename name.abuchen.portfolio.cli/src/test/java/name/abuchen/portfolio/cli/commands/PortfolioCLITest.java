package name.abuchen.portfolio.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * Tests for the Portfolio CLI.
 */
public class PortfolioCLITest
{
    @Test
    public void testHelpCommand()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try
        {
            System.setOut(new PrintStream(outputStream));
            
            PortfolioCLI cli = new PortfolioCLI();
            CommandLine cmd = new CommandLine(cli);
            int exitCode = cmd.execute("--help");
            
            assertEquals(0, exitCode);
            String output = outputStream.toString();
            assertTrue(output.contains("Portfolio Performance CLI"));
            assertTrue(output.contains("Usage:"));
        }
        finally
        {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testVersionCommand()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try
        {
            System.setOut(new PrintStream(outputStream));
            
            PortfolioCLI cli = new PortfolioCLI();
            CommandLine cmd = new CommandLine(cli);
            int exitCode = cmd.execute("--version");
            
            assertEquals(0, exitCode);
            String output = outputStream.toString();
            assertTrue(output.contains("Portfolio Performance CLI"));
        }
        finally
        {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testAccountsHelpCommand()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try
        {
            System.setOut(new PrintStream(outputStream));
            
            PortfolioCLI cli = new PortfolioCLI();
            CommandLine cmd = new CommandLine(cli);
            int exitCode = cmd.execute("accounts", "--help");
            
            assertEquals(0, exitCode);
            String output = outputStream.toString();
            assertTrue(output.contains("Manage accounts"));
        }
        finally
        {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testTransactionsHelpCommand()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try
        {
            System.setOut(new PrintStream(outputStream));
            
            PortfolioCLI cli = new PortfolioCLI();
            CommandLine cmd = new CommandLine(cli);
            int exitCode = cmd.execute("transactions", "--help");
            
            assertEquals(0, exitCode);
            String output = outputStream.toString();
            assertTrue(output.contains("Manage transactions"));
        }
        finally
        {
            System.setOut(originalOut);
        }
    }
    
    @Test
    public void testMissingFileOption()
    {
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        
        try
        {
            System.setErr(new PrintStream(errorStream));
            
            PortfolioCLI cli = new PortfolioCLI();
            CommandLine cmd = new CommandLine(cli);
            int exitCode = cmd.execute("accounts", "list");
            
            assertEquals(2, exitCode); // picocli's missing required option exit code
        }
        finally
        {
            System.setErr(originalErr);
        }
    }
}