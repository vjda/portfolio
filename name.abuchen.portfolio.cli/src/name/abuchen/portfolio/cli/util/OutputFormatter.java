package name.abuchen.portfolio.cli.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility for formatting CLI output in different formats (table, JSON).
 */
public class OutputFormatter
{
    public enum Format
    {
        TABLE, JSON
    }

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Format data as JSON.
     */
    public static String formatAsJson(Object data)
    {
        try
        {
            return JSON_MAPPER.writeValueAsString(data);
        }
        catch (Exception e)
        {
            return "Error formatting JSON: " + e.getMessage();
        }
    }

    /**
     * Format a list of maps as a simple table.
     */
    public static String formatAsTable(List<Map<String, Object>> data, String... columnHeaders)
    {
        if (data.isEmpty())
            return "No data to display";

        var sb = new StringBuilder();
        
        // Header
        if (columnHeaders.length > 0)
        {
            sb.append(String.join("\t", columnHeaders)).append("\n");
            sb.append("-".repeat(50)).append("\n");
        }

        // Data rows
        for (Map<String, Object> row : data)
        {
            if (columnHeaders.length > 0)
            {
                for (int i = 0; i < columnHeaders.length; i++)
                {
                    if (i > 0) sb.append("\t");
                    Object value = row.get(columnHeaders[i].toLowerCase());
                    sb.append(value != null ? value.toString() : "");
                }
            }
            else
            {
                // Show all key-value pairs
                row.forEach((key, value) -> 
                    sb.append(key).append(": ").append(value).append("\t"));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Format output based on the specified format.
     */
    public static String format(Object data, Format format)
    {
        switch (format)
        {
            case JSON:
                return formatAsJson(data);
            case TABLE:
                if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map)
                {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> mapList = (List<Map<String, Object>>) list;
                    return formatAsTable(mapList);
                }
                return data.toString();
            default:
                return data.toString();
        }
    }
}