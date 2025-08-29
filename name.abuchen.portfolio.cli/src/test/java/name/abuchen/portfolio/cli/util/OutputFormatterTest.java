package name.abuchen.portfolio.cli.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for the OutputFormatter.
 */
public class OutputFormatterTest
{
    @Test
    public void testFormatAsJson()
    {
        List<Map<String, Object>> testData = List.of(
            Map.of("name", "Account 1", "balance", 1000.50, "active", true),
            Map.of("name", "Account 2", "balance", 2500.75, "active", false, "note", "Test note")
        );

        String json = OutputFormatter.formatAsJson(testData);
        
        assertNotNull(json);
        assertTrue(json.contains("Account 1"));
        assertTrue(json.contains("1000.5"));
        assertTrue(json.contains("true"));
        assertTrue(json.contains("Test note"));
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }

    @Test
    public void testFormatAsTable()
    {
        List<Map<String, Object>> testData = List.of(
            Map.of("name", "Account 1", "balance", "1000.50", "currency", "USD"),
            Map.of("name", "Account 2", "balance", "2500.75", "currency", "EUR")
        );

        String table = OutputFormatter.formatAsTable(testData, "name", "currency", "balance");
        
        assertNotNull(table);
        assertTrue(table.contains("Account 1"));
        assertTrue(table.contains("USD"));
        assertTrue(table.contains("1000.50"));
        assertTrue(table.contains("Account 2"));
        assertTrue(table.contains("EUR"));
        assertTrue(table.contains("2500.75"));
        assertTrue(table.contains("name\tcurrency\tbalance"));
    }

    @Test
    public void testFormatEmptyData()
    {
        List<Map<String, Object>> emptyData = List.of();
        
        String json = OutputFormatter.formatAsJson(emptyData);
        assertEquals("[ ]", json);
        
        String table = OutputFormatter.formatAsTable(emptyData);
        assertEquals("No data to display", table);
    }

    @Test
    public void testFormatMethod()
    {
        List<Map<String, Object>> testData = List.of(
            Map.of("name", "Test", "value", 42)
        );

        String jsonResult = OutputFormatter.format(testData, OutputFormatter.Format.JSON);
        assertTrue(jsonResult.contains("Test"));
        assertTrue(jsonResult.contains("42"));
        assertTrue(jsonResult.startsWith("["));

        String tableResult = OutputFormatter.format(testData, OutputFormatter.Format.TABLE);
        assertTrue(tableResult.contains("Test"));
        assertTrue(tableResult.contains("42"));
    }
}