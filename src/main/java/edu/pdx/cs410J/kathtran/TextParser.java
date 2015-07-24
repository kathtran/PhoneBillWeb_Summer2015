package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.PhoneBillParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Implements the parse method that can be found within
 * the {@link PhoneBillParser}. It reads from a text file
 * that contains the record of some existing phone bill.
 *
 * @author Kathleen Tran
 * @version 3.0
 */
public class TextParser implements PhoneBillParser {

    /**
     * The name of the file that the phone bill is being read from.
     */
    private String fileName;

    /**
     * Parses some source and returns a phone bill.
     *
     * @return a phone bill record complete with phone call(s)
     * @throws ParserException If the source cannot be parsed
     */
    @Override
    public AbstractPhoneBill parse() throws ParserException {
        Project3 project3 = new Project3();
        PhoneBill phoneBill;
        PhoneCall phoneCall;
        File file = new File(getFileName());
        boolean fileExists = file.exists();

        String caller;
        String callee;
        String starting;
        String startDate;
        String startTime;
        String ending;
        String endDate;
        String endTime;
        try {
            if (fileExists) {
                FileReader fr = new FileReader(getFileName());
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();

                if (line != null && line.equals("CUSTOMER:")) {
                    line = br.readLine();
                    phoneBill = new PhoneBill(project3.correctNameCasing(line));
                    line = br.readLine();

                    while (line != null) {
                        if (line.equals("PHONE CALL:")) {
                            caller = br.readLine();
                            callee = br.readLine();
                            starting = br.readLine();
                            ending = br.readLine();

                            String[] start = starting.split(" ");
                            String[] end = ending.split(" ");
                            startDate = start[0];
                            startTime = start[1];
                            endDate = end[0];
                            endTime = end[1];

                            if (project3.isValidPhoneNumber(caller) &&
                                    project3.isValidPhoneNumber(callee) &&
                                    project3.isValidDateAndTime(startDate, startTime) &&
                                    project3.isValidDateAndTime(endDate, endTime)) {
                                phoneCall = new PhoneCall(caller, callee, starting, ending);
                                phoneBill.addPhoneCall(phoneCall);
                            } else {
                                System.err.println("The specified phone bill record is incorrect and/or malformatted.");
                                System.exit(1);
                            }
                            line = br.readLine();
                        } else {
                            System.err.println("The specified phone bill record contains corrupted phone call(s).");
                            System.exit(1);
                        }
                    }
                    return phoneBill;
                } else {
                    System.err.println("The file specified currently exists, but contains incompatible data. " +
                            "Please correct the error before trying again.");
                    System.exit(1);
                }
            }
        } catch (IOException ex) {
            System.err.println("Something went wrong while trying to locate the specified file. " +
                    "You may want to check the order and/or formatting of your arguments.");
            System.exit(1);
        } catch (ParseException ex) {
            System.err.println("Something went wrong while parsing a phone call date and/or time.");
            System.exit(1);
        }
        return null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
