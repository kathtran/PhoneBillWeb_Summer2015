package edu.pdx.cs410J.kathtran;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages {
    public static String getMappingCount(int count) {
        return String.format("Server contains %d customer/phone bills", count);
    }

    public static String formatCustomerPhoneBillPair(String customer, PhoneBill phoneBill) {
        return String.format("  %s's %s", customer, phoneBill.prettyPrint());
    }

    public static String missingRequiredParameter(String parameterName) {
        return String.format("\nThe required parameter \"%s\" is missing\n", parameterName);
    }

    public static String mappedCustomerPhoneBill(String customer, PhoneBill phoneBill) {
        return String.format("\nMapped %s to %s\n", customer, phoneBill.getMostRecentPhoneCall().toString());
    }
}
