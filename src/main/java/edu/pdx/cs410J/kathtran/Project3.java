package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents Project 3 and contains the main method that runs the Phone
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
 *
 * @author Kathleen Tran
 * @version 3.0
 */
public class Project3 {

    /**
     * Takes an array of arguments to populate and model a customer's phone bill. Any
     * missing and/or incorrect arguments will result in the output of a corresponding
     * system error message.
     *
     * @param args options or arguments for the phone bill, or any combination of both.
     */
    public static void main(String[] args) {

        try {
            Project3 project3 = new Project3();

            for (String arg : args) {
                if (arg.equals("-README"))
                    project3.readme();
            }

            boolean printCall = false;                  // Markers used to check
            boolean loadPhoneBill = false;              // for the presence of
            boolean prettyPrintToFile = false;          // each option in the
            boolean prettyPrintToStdOut = false;        // arguments provided
            String fileName = null;
            String prettyFile = null;
            int index = 0;
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-print")) {
                    printCall = true;
                    index += 1;
                } else if (args[i].equals("-textFile") && args[i + 1] != null) {
                    loadPhoneBill = true;
                    fileName = project3.correctExtension(args[i + 1]);
                    index += 2;
                } else if (args[i].equals("-pretty") && args[i + 1] != null) {
                    if (args[i + 1].equals("-"))
                        prettyPrintToStdOut = true;
                    else {
                        prettyPrintToFile = true;
                        prettyFile = project3.correctExtension(args[i + 1]);
                    }
                    index += 2;
                }
            }

            if (fileName != null && prettyFile != null && fileName.equals(prettyFile)) {
                System.err.println("The same file name may not be used for two different options. " +
                        "Please rename one of them and try again.");
                System.exit(1);
            }

            for (String arg : args) {
                if (arg.startsWith("-") && !arg.equals("-print") &&
                        !arg.equals("-textFile") && !arg.equals("-pretty")) {
                    if ((fileName != null && fileName.contains("-") && !arg.equals(fileName)) ||
                            (prettyFile != null && prettyFile.contains("-") && !arg.equals(prettyFile))) {
                        System.err.println("Unknown command line option");
                        System.exit(1);
                    }
                }
            }

            TextParser textParser = new TextParser();
            TextDumper textDumper = new TextDumper();
            boolean fileExists = false;
            PhoneBill phoneBill = null;
            if (loadPhoneBill && fileName != null) {
                textParser.setFileName(fileName);
                textDumper.setFileName(fileName);

                File fileCheckBefore = new File(fileName);
                fileExists = fileCheckBefore.exists();

                phoneBill = (PhoneBill) textParser.parse();
            }

            if (args[index] != null && args[index].length() > 1) {
                if (!fileExists)
                    phoneBill = new PhoneBill(project3.correctNameCasing(args[index]));
                index += 1;
            } else {
                System.err.println("Cannot identify the customer name. " +
                        "You may want to check the order and/or formatting of your arguments.");
                System.exit(1);
            }

            String callerNumber = null;     // Temporary Strings used to
            String calleeNumber = null;     // hold each requirement of
            String startTime = null;        // the phone call record.
            String endTime = null;
            if (args[index] != null && project3.isValidPhoneNumber(args[index])) {
                callerNumber = args[index];
                index += 1;
            } else {
                System.err.println("Cannot identify the customer name and/or caller number. " +
                        "You may want to check the order and/or formatting of your arguments.");
                System.exit(1);
            }
            if (args[index] != null && project3.isValidPhoneNumber(args[index])) {
                calleeNumber = args[index];
                index += 1;
            } else {
                System.err.println("Cannot identify the callee number. " +
                        "You may want to check the order and/or formatting of your arguments.");
                System.exit(1);
            }
            if (args[index] != null && args[index + 1] != null && project3.isValidDateAndTime(args[index], args[index + 1])) {
                startTime = args[index];
                startTime = startTime.concat(" " + args[index + 1]);
                index += 2;
            } else {
                System.err.println("Cannot identify the start time. " +
                        "You may want to check the order and/or formatting of your arguments.");
                System.exit(1);
            }
            if (args[index] != null && args[index + 1] != null && project3.isValidDateAndTime(args[index], args[index + 1])) {
                endTime = args[index];
                endTime = endTime.concat(" " + args[index + 1]);
                index += 2;
            } else {
                System.err.println("Cannot identify the end time. " +
                        "You may want to check the order and/or formatting of your arguments.");
                System.exit(1);
            }
            if (index < args.length) {
                System.err.print("Extraneous and/or malformatted command line arguments");
                System.exit(1);
            }

            PhoneCall phoneCall = new PhoneCall(callerNumber, calleeNumber, startTime, endTime);
            phoneBill.addPhoneCall(phoneCall);

            if (printCall)
                System.out.println(phoneBill.getMostRecentPhoneCall().toString());
            phoneBill.sortPhoneCalls();
            if (loadPhoneBill) {
                File fileCheckAfter = new File(textDumper.getFileName());
                fileExists = fileCheckAfter.exists();
                if (fileExists) {
                    if (textDumper.checkCustomerName(phoneBill.getCustomer()))
                        textDumper.dump(phoneBill);
                    else {
                        System.err.println("The file name specified already exists! However, it belongs to a different customer. " +
                                "\nThe phone bill record was not saved. You may either specify a different file name or\n" +
                                "check to make sure that your supplied customer name matches the one on file.");
                        System.exit(1);
                    }
                } else
                    textDumper.dump(phoneBill);
            }
            if (prettyPrintToFile)
                textDumper.prettyDumper(prettyFile, phoneBill);
            else if (prettyPrintToStdOut)
                System.out.print(phoneBill.prettyPrint());
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Missing and/or malformatted command line arguments");
            System.exit(1);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid and/or malformatted date(s) entered");
            System.exit(1);
        } catch (ParseException ex) {
            System.err.println("Invalid date(s) entered");
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Invalid and/or malformatted text file");
            System.exit(1);
        } catch (ParserException ex) {
            System.err.println("Something went wrong whilst attempting to parse the specified file");
            System.exit(1);
        }
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
     * @return True if the both the date and formatting are valid, otherwise false
     * @throws NumberFormatException when the argument cannot be parsed into an Integer
     * @throws ParseException        when the date is invalid
     */
    public boolean isValidDateAndTime(String dateInput, String timeInput) throws NumberFormatException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        dateFormat.parse(dateInput);
        return isValidTimeOfDay(timeInput);
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
        Pattern timeFormat = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");
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

                "Commands\n" +
                "--------\n\n" +
                "-README\t\t\t\tThe project description. Entering this option at\n" +
                "\t\t\t\t\tthe command line will display this page.\n" +
                "-print\t\t\t\tA description of some phone call. Entering this\n" +
                "\t\t\t\t\toption at the command line will display the\n" +
                "\t\t\t\t\tdescription of the most recently added phone call.\n" +
                "-textFile <file>\tWhere to read/write the phone bill\n" +
                "-pretty <file/->\tWhere to pretty print the phone bill\n\n" +
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
                "CS410J PROJECT 3: PRETTY PRINTING A PHONE BILL\n\n" +
                "AUTHOR: KATHLEEN TRAN\nLAST MODIFIED: 7/21/2015\n\n");
        System.exit(1);
    }
}