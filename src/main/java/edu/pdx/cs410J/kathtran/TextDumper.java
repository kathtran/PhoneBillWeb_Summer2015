package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.PhoneBillDumper;

import java.io.*;
import java.util.ArrayList;

/**
 * Implements the dump method that can be found within
 * the {@link PhoneBillDumper}. It creates a text file
 * that contains the records of a provided phone bill.
 *
 * v3.0 UPDATE: In addition to the dump method, a pretty
 * dumper has been implemented to output the contents of
 * a nicely formatted phone bill into a text file.
 *
 * @author Kathleen Tran
 * @version 3.0
 */
public class TextDumper implements PhoneBillDumper {

    /**
     * The name of the file that the phone bill is being saved to.
     */
    private String fileName;

    /**
     * Verifies that the customer name supplied at the command line is
     * the same as the one on record if the record currently exists.
     *
     * @param customer some name provided by the user
     * @return true if the name matches the existing record, otherwise false
     * @throws IOException if the file cannot be found
     */
    boolean checkCustomerName(String customer) throws IOException {
        File file = new File(getFileName());
        boolean fileExists = file.exists();

        if (fileExists) {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();

            while (line != null) {
                if (line.equals("CUSTOMER:")) {
                    line = br.readLine();
                    return customer.equals(line);
                }
            }
            br.close();
            fr.close();
        }
        return false;
    }

    /**
     * Dumps a phone bill to some destination specified by the user.
     *
     * @param bill a phone bill for some customer that contains at least one phone call record
     * @throws IOException if file cannot be found
     */
    @Override
    public void dump(AbstractPhoneBill bill) throws IOException {
        @SuppressWarnings("unchecked")
        ArrayList<PhoneCall> phoneBill = (ArrayList<PhoneCall>) bill.getPhoneCalls();
        File file = new File(getFileName());
        boolean fileExists = file.exists();

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("CUSTOMER:\n" + bill.getCustomer() + "\n");
        for (PhoneCall call : phoneBill) {
            bw.write("PHONE CALL:\n");
            bw.write(call.getCaller() + "\n");
            bw.write(call.getCallee() + "\n");
            bw.write(call.getStartTimeString() + "\n");
            bw.write(call.getEndTimeString() + "\n");
        }
        bw.close();
    }

    /**
     * Dumps a pretty phone bill to some destination specified by the user.
     *
     * @param bill a phone bill for some customer that contains at least one phone call record
     * @throws IOException if file cannot be found
     */
    public void prettyDumper(String fileName, AbstractPhoneBill bill) throws IOException {
        PhoneBill phoneBill = (PhoneBill) bill;
        File file = new File(fileName);
        boolean fileExists = file.exists();

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(phoneBill.prettyPrint());
        bw.close();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
