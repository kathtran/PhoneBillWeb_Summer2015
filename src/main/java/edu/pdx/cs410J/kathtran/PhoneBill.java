package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Implements the abstract methods that can be found within the
 * {@link AbstractPhoneBill} in addition to new methods that support
 * the construction of the phone bill skeleton. The customer's
 * name as well as their collection of phone call records are
 * maintained here.
 *
 * v3.0 UPDATE: A pretty print method has been implemented. It
 * nicely formats the phone bill and its corresponding phone calls.
 *
 * @author Kathleen Tran
 * @version 3.0
 */
public class PhoneBill extends AbstractPhoneBill {

    /**
     * The customer's name. May consist of one or more words,
     * and be comprised of any character, symbol, or number.
     */
    private String customer;

    /**
     * All phone call records that are associated with the
     * customer. Each record, or item, is an instance of the
     * {@link PhoneCall} class.
     */
    private ArrayList phoneCalls;

    /**
     * Default constructor.
     */
    public PhoneBill() {
        this.customer = null;
        this.phoneCalls = new ArrayList<PhoneCall>();
    }

    /**
     * Constructor that specifies the customer's name.
     *
     * @param customer a name that may consist of one or more
     *                 words, as some String
     */
    public PhoneBill(String customer) {
        this.customer = customer;
        this.phoneCalls = new ArrayList<PhoneCall>();
    }

    /**
     * @return the name of the customer whose phone bill this is
     */
    @Override
    public String getCustomer() {
        return this.customer;
    }

    /**
     * Adds a phone call record to this phone bill.
     *
     * @param call an instance of the {@link PhoneCall} class that
     *             contains the caller's phone number, callee's phone
     *             number, and start and end times of the call
     */
    @Override
    public void addPhoneCall(AbstractPhoneCall call) {
        this.phoneCalls.add(call);
    }

    /**
     * @return all of the phone calls (as instances of {@link
     * AbstractPhoneCall}) in this phone bill
     */
    @Override
    public Collection getPhoneCalls() {
        return this.phoneCalls;
    }

    /**
     * Gets the call record for the most recent phone call made.
     *
     * @return the call record at the end of the list
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public Object getMostRecentPhoneCall() throws ArrayIndexOutOfBoundsException {
        return this.phoneCalls.get(phoneCalls.size() - 1);
    }

    /**
     * Sorts the phone calls in the phone bill by starting time. Ties are
     * broken by comparing the callers' phone numbers.
     */
    public void sortPhoneCalls() {
        Collections.sort(this.phoneCalls);
    }

    /**
     * Prints out the phone bill and all of its call records in
     * a user-friendly format.
     *
     * @return the entire phone bill in its new pretty format
     */
    public String prettyPrint() {
        String entireBill = "CS410J Phone Bill\n" + "=================\n" + this.getCustomer() +
                "\nNo. of Calls on Record: " + this.phoneCalls.size() +
                "\n\nDate(s)\t\t\tCaller\t\t\t\tCallee\t\t\t\tCall Began\t\t\tCall Ended\t\t\tDuration (mins)";
        for (Object call : getPhoneCalls()) {
            PhoneCall phoneCall = (PhoneCall) call;
            entireBill = entireBill.concat(phoneCall.prettyPrint());
        }
        return entireBill;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
