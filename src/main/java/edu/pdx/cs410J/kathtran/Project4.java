package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents Project 4 and contains the main method that runs the Phone
 * Bill Application in addition to various helper methods that correct
 * and/or validate user-supplied command line arguments that are used to
 * construct and populate the phone bill.
 * <p>
 * v2.0 UPDATE: There now exists calls to methods that handle working
 * with external files for both importing and exporting phone bill records.
 * <p>
 * v3.0 UPDATE: In addition to the pretty printer, changes have also been
 * made to the date and time formatting of each phone call, and the calls
 * under each phone bill are now sorted by the times at which the they
 * began.
 * <p>
 * v4.0 UPDATE: Server/client communication using REST established.
 *
 * @author Kathleen Tran
 * @version 4.0
 */
public class Project4 {

    private static String hostName;
    private static String portString;
    private static boolean hostFlag = false;
    private static boolean portFlag = false;
    private static int port;

    private static String searchAfter;
    private static String searchBefore;
    private static boolean search = false;

    private static PhoneBill phoneBill;
    private static PhoneCall phoneCall;
    private static String customer;
    private static String callerNumber;
    private static String calleeNumber;
    private static String startTime;
    private static String endTime;
    private static boolean printCall = false;
    private static int index = 0;

    public static final String MISSING_ARGS = "Missing command line arguments";

    /**
     * Takes an array of arguments to populate and model a customer's phone bill. Any
     * missing and/or incorrect arguments will result in the output of a corresponding
     * system error message.
     *
     * @param args options or arguments for the phone bill, or any combination of both.
     */
    public static void main(String... args) {

        Project4 project4 = new Project4();

        for (String arg : args) {
            if (arg.equals("-README"))
                project4.readme();
        }

        for (String arg : args) {
            if (arg.equals("-print")) {
                printCall = true;
                index += 1;
            }
        }

        for (String arg : args) {
            if (arg.startsWith("-") && !arg.equals("-print") &&
                    !arg.equals("-host") && !arg.equals("-port") && !arg.equals("-search")) {
                System.err.println("Unknown command line option");
                System.exit(1);
            }
        }

        if (args[index] != null && args[index].equals("-host")) {
            hostFlag = true;
            index += 1;
            if (args[index] != null && !args[index].equals("-port") &&
                    !args[index].equals("-search") && !args[index].equals("-print")) {
                hostName = args[index];
                index += 1;
            } else {
                System.err.println("Missing and/or malformatted hostname");
                System.exit(1);
            }
        }
        if (args[index] != null && args[index].equals("-port")) {
            portFlag = true;
            index += 1;
            if (args[index] != null && !args[index].equals("-host") &&
                    !args[index].equals("-search") && !args[index].equals("-print")) {
                portString = args[index];
                index += 1;
            } else {
                System.err.println("Missing and/or malformatted port");
                System.exit(1);
            }
        }
        if (args[index] != null && args[index].equals("-search")) {
            search = true;
            index += 1;
            if (args[index] != null && !args[index].equals("-host") &&
                    !args[index].equals("-port") && !args[index].equals("-print")) {
                customer = args[index];
                try {
                    if (args[index + 1] != null && args[index + 2] != null && args[index + 3] != null &&
                            args[index + 4] != null && args[index + 5] != null && args[index + 6] != null
                            && project4.isValidDateAndTime(args[index + 1], args[index + 2], args[index + 3])
                            && project4.isValidDateAndTime(args[index + 4], args[index + 5], args[index + 6])) {
                        searchAfter = args[index + 1] + " " + args[index + 2] + " " + args[index + 3];
                        searchBefore = args[index + 4] + " " + args[index + 5] + " " + args[index + 6];
                    }
                } catch (ParseException ex) {
                    System.err.println("Invalid date(s) entered");
                    System.exit(1);
                }
                Project4.index += 6;
            } else {
                System.err.println("Missing and/or malformatted search criteria");
                System.exit(1);
            }
        }

        // Ensure that both a host name and a valid port have been given if the flags are set
        if (hostFlag || portFlag) {
            if (hostName == null && portString != null)
                usage("Missing host");
            else if (portString == null && hostName != null)
                usage("Missing port");
            else if (portString != null) {
                try {
                    port = Integer.parseInt(portString);
                } catch (NumberFormatException ex) {
                    usage("Port \"" + portString + "\" must be an integer");
                    return;
                }
            }
        }

        //************************** PARSING ARGUMENTS FOR PHONE CALL **************************//

        if (!search) {
            try {
                if (args[index] != null) {
                    customer = project4.correctNameCasing(args[index]);
                    phoneBill = new PhoneBill(customer);
                    index += 1;
                } else {
                    System.err.println("Cannot identify the customer name. " +
                            "You may want to check the order and/or formatting of your arguments.");
                    System.exit(1);
                }
                if (args[index] != null && project4.isValidPhoneNumber(args[index])) {
                    callerNumber = args[index];
                    index += 1;
                } else {
                    System.err.println("Cannot identify the customer name and/or caller number. " +
                            "You may want to check the order and/or formatting of your arguments.");
                    System.exit(1);
                }
                if (args[index] != null && project4.isValidPhoneNumber(args[index])) {
                    calleeNumber = args[index];
                    index += 1;
                } else {
                    System.err.println("Cannot identify the callee number. " +
                            "You may want to check the order and/or formatting of your arguments.");
                    System.exit(1);
                }
                if (args[index] != null && args[index + 1] != null && args[index + 2] != null &&
                        project4.isValidDateAndTime(args[index], args[index + 1], args[index + 2].toUpperCase())) {
                    startTime = args[index] + " " + args[index + 1] + " " + args[index + 2];
                    index += 3;
                } else {
                    System.err.println("Cannot identify the start time. " +
                            "You may want to check the order and/or formatting of your arguments.");
                    System.exit(1);
                }
                if (args[index] != null && args[index + 1] != null && args[index + 2] != null &&
                        project4.isValidDateAndTime(args[index], args[index + 1], args[index + 2].toUpperCase())) {
                    endTime = args[index] + " " + args[index + 1] + " " + args[index + 2];
                    index += 3;
                } else {
                    System.err.println("Cannot identify the end time. " +
                            "You may want to check the order and/or formatting of your arguments.");
                    System.exit(1);
                }
                if (index < args.length) {
                    System.err.print("Extraneous and/or malformatted command line arguments");
                    System.exit(1);
                }

                phoneCall = new PhoneCall(callerNumber, calleeNumber, startTime, endTime);
                phoneBill.addPhoneCall(phoneCall);

                if (printCall)
                    System.out.println(phoneBill.getMostRecentPhoneCall().toString());
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.err.println("Missing and/or malformatted command line arguments");
                System.exit(1);
            } catch (NumberFormatException ex) {
                System.err.println("Invalid and/or malformatted date(s) entered");
                System.exit(1);
            } catch (ParseException ex) {
                System.err.println("Invalid date(s) entered");
                System.exit(1);
            }
        }

        if (hostFlag && portFlag) {
            PhoneBillRestClient client = new PhoneBillRestClient(hostName, port);
            HttpRequestHelper.Response response;
            try {
                if (customer == null)
                    response = client.getAllCustomersAndPhoneBills();
                else if (phoneBill == null)
                    response = client.getPhoneBills(customer);
                else if (search && searchAfter != null && searchBefore != null)
                    response = client.getSearchedPhoneBills(customer, searchAfter, searchBefore);
                else
                    response = client.addCustomerPhoneCallPair(customer, phoneCall);
                checkResponseCode(HttpURLConnection.HTTP_OK, response);
            } catch (IOException ex) {
                error("While contacting server: " + ex);
                return;
            }
            System.out.println(response.getContent());
        }
        System.exit(0);
    }

    /**
     * Makes sure that the give response has the expected HTTP status code
     *
     * @param code     The expected status code
     * @param response The response from the server
     */
    private static void checkResponseCode(int code, HttpRequestHelper.Response response) {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                    response.getCode(), response.getContent()));
        }
    }

    private static void error(String message) {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     *
     * @param message An error message to print
     */
    private static void usage(String message) {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port [key] [value]");
        err.println("  host    Host of web server");
        err.println("  port    Port of web server");
        err.println("  key     Key to query");
        err.println("  value   Value to add to server");
        err.println();
        err.println("This simple program posts key/value pairs to the server");
        err.println("If no value is specified, then all values are printed");
        err.println("If no key is specified, all key/value pairs are printed");
        err.println();

        System.exit(1);
    }

    /**
     * Corrects the casing of some <code>String</code> that is the customer's name.
     *
     * @param nameInput some name provided by the user
     * @return a String where the first letter of each name is capitalized while
     * the remaining letters are lower cased. Each part of the name is separated
     * by a single whitespace.
     */

    public String correctNameCasing(String nameInput) {
        @SuppressWarnings("all")
        String correctedName = new String();
        String[] fullName = nameInput.split(" ");
        for (String name : fullName) {
            char firstLetter = Character.toUpperCase(name.charAt(0));
            String remainingLetters = name.substring(1).toLowerCase();
            correctedName = correctedName.concat(firstLetter + remainingLetters + " ");
        }
        return correctedName.substring(0, correctedName.length() - 1);
    }

    /**
     * Determines whether or not some <code>String</code> is of the form
     * <code>nnn-nnn-nnnn</code> where <code>n</code> is a number <code>0-9</code>.
     *
     * @param phoneNumberInput some phone number provided by the user
     * @return True if the form is valid, otherwise false
     */
    public boolean isValidPhoneNumber(String phoneNumberInput) {
        Pattern phoneNumberFormat = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher numberToBeChecked = phoneNumberFormat.matcher(phoneNumberInput);
        return numberToBeChecked.matches();
    }

    /**
     * Determines the validity of some <code>String</code> representative of the date
     * and time both in regards to the values provided and to their formatting.
     *
     * @param dateInput the month, day, and year
     * @param timeInput the hour and minute(s)
     * @param timeMark  am/pm marker
     * @return True if the both the date and formatting are valid, otherwise false
     * @throws NumberFormatException when the argument cannot be parsed into an Integer
     * @throws ParseException        when the date is invalid
     */
    public boolean isValidDateAndTime(String dateInput, String timeInput, String timeMark) throws
            NumberFormatException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        dateFormat.parse(dateInput);
        return isValidTimeOfDay(timeInput) && (timeMark.equals("AM") || timeMark.equals("PM"));
    }

    /**
     * Determines whether or not the time of some <code>String</code> is
     * of the form <code>hh:mm</code> where the hour may be one digit if
     * it is less than the value of nine.
     *
     * @param timeToCheck time
     * @return True if the form is valid, otherwise false
     */
    public boolean isValidTimeOfDay(String timeToCheck) {
        Pattern timeFormat = Pattern.compile("(0?[1-9]|1[0-2]):[0-5][0-9]");
        Matcher timeToBeChecked = timeFormat.matcher(timeToCheck);
        return timeToBeChecked.matches();
    }

    /**
     * Adds the plain text file type extension to a file name if one
     * has not already been appended.
     *
     * @param fileName some name for a file
     * @return the file name with the plain text file type extension
     */
    public String correctExtension(String fileName) {
        if (fileName.contains(".txt"))
            return fileName;
        else
            return fileName + ".txt";
    }

    /**
     * Prints out a brief description of what the Phone Bill Application is and how it operates.
     */
    public void readme() {
        System.out.print("\n\t\tREADME - PHONE BILL APPLICATION\n" +
                "\t\t===============================\n" +
                "Introduction\n" +
                "------------\n\n" +
                "Welcome to the Phone Bill Application! This is a command-line\n" +
                "application that allows the user to model a phone bill. In version\n" +
                "1.0, the user may associate at most one phone record per customer\n" +
                "name. However, the information will not be stored between each usage.\n" +
                "A single phone record consists of the phone number of the caller, the\n" +
                "phone number that was called, the time at which the call began, and\n" +
                "the time at which the call ended.\n\n" +

                "Updates\n" +
                "-------\n" +
                "v2.0\tThe program now allows the user to save their phone bill\n" +
                "\t\tto an external text file (both new and existing). Users may\n" +
                "\t\talso load phone bill records from existing files. Each file\n" +
                "\t\tcorrelates to a single user and their phone call(s).\n\n" +
                "v3.0\tThe newest feature that has been added is the option to have\n" +
                "\t\tthe phone bill be printed out in a more user-friendly format,\n" +
                "\t\tto either a separate text file or to standard out, complete\n" +
                "\t\twith the duration of each phone call in minutes. Phone calls\n" +
                "\t\twithin the phone bills are now listed chronologically by their\n" +
                "\t\tbeginning time, with the phone numbers serving as tie-breakers\n" +
                "\t\tin appropriate cases. In addition, time stamps are no longer\n" +
                "\t\trecorded in 24-hour time.\n\n" +
                "v4.0\tA server/client has now been established using REST to incorporate\n" +
                "\t\ta web service to the program. Users may add phone bills to the\n" +
                "\t\tserver and search for phone calls belonging to some given phone bill\n" +
                "\t\tbetween some given time span.\n\n" +

                "Commands\n" +
                "--------\n\n" +
                "-README\t\t\t\tThe project description. Entering this option at\n" +
                "\t\t\t\t\tthe command line will display this page.\n" +
                "-print\t\t\t\tA description of some phone call. Entering this\n" +
                "\t\t\t\t\toption at the command line will display the\n" +
                "\t\t\t\t\tdescription of the most recently added phone call.\n" +
                "-search\t\t\t\tThis option followed by a customer name, some\n" +
                "\t\t\t\t\tstarting time and some ending time will return all\n" +
                "\t\t\t\t\tof the calls started between those times.\n" +
                "-host <hostname>\tThe host computer on which the server runs.\n" +
                "-port <port>\t\tThe port on which the server is listening.\n" +
                "To add a calling event, the following arguments must be provided\n" +
                "in the order listed below, separated by a single white space.\n\n" +
                "<customer>\t\t\tPerson whose phone bill we're modelling\n" +
                "<caller number>\t\tPhone number of the caller\n" +
                "<callee number>\t\tPhone number of the person called\n" +
                "<start time>\t\tDate and time the call began\n" +
                "<end time>\t\t\tDate and time the call ended\n\n" +
                "If the customer name contains more than one word, it must be\n" +
                "delimited by double quotes. Phone numbers must be of the form\n" +
                "nnn-nnn-nnnn where n is a number 0-9. Date and time should be\n" +
                "in the format: mm/dd/yyyy hh:mm and zeros may be omitted where\n" +
                "appropriate.\n\n" +
                "Options are to be entered before arguments, and only the customer\n" +
                "name may be delimited by double quotes.\n" +
                "\n" +
                "----------------------------------------------------------\n" +
                "CS410J PROJECT 4: A REST-FULL PHONE BILL WEB SERVICE\n\n" +
                "AUTHOR: KATHLEEN TRAN\nLAST MODIFIED: 7/28/2015\n\n");
        System.exit(1);
    }
}