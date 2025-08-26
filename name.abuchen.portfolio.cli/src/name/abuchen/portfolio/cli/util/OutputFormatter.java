package name.abuchen.portfolio.cli.util;

import java.util.List;
import java.util.Map;

/**
 * Utility for formatting CLI output in different formats (table, JSON).
 */
public class OutputFormatter
{
    public enum Format
    {
        TABLE, JSON
    }

    /**
     * Format data as JSON (simple implementation without external dependencies).
     */
    public static String formatAsJson(Object data)
    {
        if (data instanceof List<?> list)
        {
            var sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < list.size(); i++)
            {
                if (i > 0) sb.append(",\n");
                sb.append("  ").append(formatObjectAsJson(list.get(i)));
            }
            sb.append("\n]");
            return sb.toString();
        }
        return formatObjectAsJson(data);
    }

    private static String formatObjectAsJson(Object obj)
    {
        if (obj instanceof Map<?, ?> map)
        {
            var sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                if (!first) sb.append(", ");
                first = false;
                sb.append("\"").append(entry.getKey()).append("\": ");
                Object value = entry.getValue();
                if (value instanceof String)
                {
                    sb.append("\"").append(escapeJsonString(value.toString())).append("\"");
                }
                else if (value instanceof Number || value instanceof Boolean)
                {
                    sb.append(value);
                }
                else if (value == null)
                {
                    sb.append("null");
                }
                else
                {
                    sb.append("\"").append(escapeJsonString(value.toString())).append("\"");
                }
            }
            sb.append("}");
            return sb.toString();
        }
        else if (obj instanceof String)
        {
            return "\"" + escapeJsonString(obj.toString()) + "\"";
        }
        else if (obj instanceof Number || obj instanceof Boolean)
        {
            return obj.toString();
        }
        else if (obj == null)
        {
            return "null";
        }
        else
        {
            return "\"" + escapeJsonString(obj.toString()) + "\"";
        }
    }

    private static String escapeJsonString(String str)
    {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
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