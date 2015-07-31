package edu.pdx.cs410J.kathtran;

/**
 * Class for formatting messages on the server side. This is mainly to enable
 * test methods that validate that the server returned expected strings.
 *
 * @author Kathleen Tran
 * @version 4.0
 */
public class Messages {

    /**
     * The number of mappings currently existing in the server. Mappings are
     * from a customer to his or her phone bill.
     *
     * @param count number of mappings
     * @return a formatted message that outputs the count
     */
    public static String getMappingCount(int count) {
        return String.format("Server contains %d customer/phone bills", count);
    }

    /**
     * A customer and his or her phone bill.
     *
     * @param customer  some name
     * @param phoneBill a record that consists of all calling events
     * @return a formatted message that outputs the customer's name along with
     * the corresponding phone bill, pretty printed
     */
    public static String formatCustomerPhoneBillPair(String customer, PhoneBill phoneBill) {
        return String.format("  %s's %s", customer, phoneBill.prettyPrint());
    }

    /**
     * Notification of a missing parameter.
     *
     * @param parameterName some parameter
     * @return a formatted message that makes note of the missing parameter
     */
    public static String missingRequiredParameter(String parameterName) {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    /**
     * Notification of a successful mapping.
     *
     * @param customer  some name
     * @param phoneBill a record that consists of all calling events
     * @return a formatted message that makes note of the successful mapping
     */
    public static String mappedCustomerPhoneBill(String customer, PhoneBill phoneBill) {
        return String.format("Mapped %s to %s", customer, phoneBill.getMostRecentPhoneCall().toString());
    }

    /**
     * Search results for phone calls started during some duration specified
     * by the user, for the specified customer.
     *
     * @param customer some name
     * @return a formatted message that consists of all the phone calls found
     */
    public static String searchPhoneBillForCalls(String customer) {
        return String.format("Phone calls that exist for %s between the designated times" +
                "\n  Date(s)\tCaller\t\tCallee\t\tCall Began\tCall Ended\tDuration (mins)", customer);
    }

    /**
     * Notification of no phone calls found.
     *
     * @param customer some name
     * @return a formatted message noted that no phone calls were found via
     * the search feature
     */
    public static String noCallsFound(String customer) {
        return String.format("No call records were found within %s's phone bill between the designated times", customer);
    }
}
