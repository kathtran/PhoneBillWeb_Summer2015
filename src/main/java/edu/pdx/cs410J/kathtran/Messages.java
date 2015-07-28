package edu.pdx.cs410J.kathtran;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{
    public static String getMappingCount( int count )
    {
        return String.format( "Phonebill contains %d customer/phonecall records", count );
    }

    public static String formatCustomerPhoneBillPair(String key, PhoneBill value)
    {
        return String.format("  %s's %s", key, value.prettyPrint());
    }

    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    public static String mappedCustomerPhoneBill(String key, PhoneBill value)
    {
        return String.format( "Mapped %s to %s", key, value.getMostRecentPhoneCall().toString() );
    }
}
