package name.abuchen.portfolio.cli.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.MapConverter;

import name.abuchen.portfolio.cli.model.Client;

/**
 * Utility to configure XStream for Portfolio Performance files.
 * Simplified version for CLI that works with simplified model classes.
 */
public class XStreamConfigurationUtil
{
    /**
     * Create and configure an XStream instance for reading Portfolio Performance files.
     */
    public static XStream createConfiguredXStream()
    {
        var xstream = new XStream();

        // Security configuration - allow our model classes
        xstream.allowTypesByWildcard(new String[] { 
            "name.abuchen.portfolio.cli.model.**",
            "java.time.**",
            "java.util.**",
            "java.lang.**"
        });

        // Set class loader
        xstream.setClassLoader(XStreamConfigurationUtil.class.getClassLoader());

        // Configure immutable types for backward compatibility
        xstream.addImmutableType(LocalDate.class, true);
        xstream.addImmutableType(LocalDateTime.class, true);

        // Register converters
        xstream.registerConverter(new XStreamLocalDateConverter());
        xstream.registerConverter(new XStreamLocalDateTimeConverter());
        xstream.registerConverter(new XStreamInstantConverter());

        // Configure main entity aliases - map XML elements to our simplified classes
        xstream.alias("client", Client.class);
        xstream.alias("account", Client.Account.class);
        xstream.alias("portfolio", Client.Portfolio.class);
        xstream.alias("security", Client.Security.class);
        xstream.alias("account-transaction", Client.AccountTransaction.class);
        xstream.alias("portfolio-transaction", Client.PortfolioTransaction.class);

        // Set the mode to ignore unknown elements (more lenient parsing)
        xstream.ignoreUnknownElements();
        
        return xstream;
    }

    /**
     * Create XStream instance configured for writing (with immutable types for performance).
     */
    public static XStream createWriterXStream()
    {
        XStream xstream = createConfiguredXStream();

        // Add immutable types to skip ID attributes for better XML readability
        xstream.addImmutableType(HashMap.class, false);
        xstream.addImmutableType(ArrayList.class, false);

        return xstream;
    }
}