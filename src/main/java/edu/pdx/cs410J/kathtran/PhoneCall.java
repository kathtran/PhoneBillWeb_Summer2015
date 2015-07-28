package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements the abstract methods that can be found within
 * the {@link AbstractPhoneCall}. It represents a single
 * phone call record, in which there exists a caller number,
 * callee number, start time, and end time.
 * <p>
 * v3.0 UPDATE: Various methods have been implemented to support
 * better formatted dates and times, and to pretty print the
 * details of the phone call.
 *
 * @author Kathleen Tran
 * @version 4.0
 */
class PhoneCall extends AbstractPhoneCall implements Comparable {

    /**
     * The phone number of the caller
     */
    private String callerNumber;

    /**
     * The phone number of the person who was called
     */
    private String calleeNumber;

    /**
     * The time at which the call began
     */
    private String startTime;

    /**
     * The time at which the call ended
     */
    private String endTime;

    /**
     * Default constructor.
     */
    public PhoneCall() {
        this.callerNumber = null;
        this.calleeNumber = null;
        this.startTime = null;
        this.endTime = null;
    }

    /**
     * Constructor that specifies all of the fields existent within a call record.
     *
     * @param callerNumber the number of the person who called
     * @param calleeNumber the number of the person who was called
     * @param startTime    the time at which the call began
     * @param endTime      the time at which the call ended
     */
    public PhoneCall(String callerNumber, String calleeNumber, String startTime, String endTime) {
        this.callerNumber = callerNumber;
        this.calleeNumber = calleeNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PhoneCall(String call) {
        String[] split = call.split(" ");
        callerNumber = split[0];
        calleeNumber = split[1];
        startTime = split[2] + " " + split[3] + " " + split[4];
        endTime = split[5] + " " + split[6] + " " + split[7];
    }

    /**
     * @return the phone number of the person who originated this phone call
     */
    @Override
    public String getCaller() {
        return this.callerNumber;
    }

    /**
     * @return the phone number of the person who received this phone call
     */
    @Override
    public String getCallee() {
        return this.calleeNumber;
    }

    /**
     * @return a textual representation of the time that this phone call
     * was originated
     */
    @Override
    public String getStartTimeString() {
        return dateFormatter(this.startTime);
    }

    /**
     * @return a textual representation of the time that this phone call
     * was completed
     */
    @Override
    public String getEndTimeString() {
        return dateFormatter(this.endTime);
    }

    /**
     * Formats the date and time to reflect the requirements of Project 3,
     * where the date remains formatted as MM/dd/yyyy, while the time is
     * in a 12-hour format and includes AM/PM.
     *
     * @param dateToFormat some date and time
     * @return date and time formatted using java.text.DateFormat.SHORT
     */
    private String dateFormatter(String dateToFormat) {
        Date date = null;
        DateFormat parseDate = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        try {
            date = parseDate.parse(dateToFormat);
        } catch (ParseException ex) {
            System.err.println("Something went wrong whilst attempting to parse the date");
            System.exit(1);
        }
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
    }

    /**
     * Calculate the duration of the phone call.
     *
     * @return the duration of the call, in minutes.
     */
    public long getCallDuration() {
        Date start = getDateObject(startTime);
        Date end = getDateObject(endTime);
        long duration = end.getTime() - start.getTime();
        return (duration / (60 * 1000));
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param object the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Object object) throws NullPointerException, ClassCastException {
        PhoneCall comparison = (PhoneCall) object;

        Date thisDate = getDateObject(this.getStartTimeString());
        Date thatDate = getDateObject(comparison.getStartTimeString());

        if (thisDate.equals(thatDate)) {
            int numberCompareResult = comparePhoneNumbers(comparison.getCaller());
            if (numberCompareResult == 0) {
                return 0;
            } else
                return numberCompareResult;
        } else if (thisDate.before(thatDate))
            return -1;
        else
            return 1;
    }

    /**
     * Creates a date object of some given date and time.
     *
     * @param dateToGet some date and time
     * @return a Date object of the provided date and time
     */
    private Date getDateObject(String dateToGet) {
        Date date = null;
        DateFormat parseDate = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        try {
            date = parseDate.parse(dateToGet);
        } catch (ParseException ex) {
            System.err.println("Something went wrong whilst attempting to parse the date");
            System.exit(1);
        }
        return date;
    }

    /**
     * Compares this object's caller number with the specified object's caller number.
     * Returns a negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param phoneNumberToBeCompared some phone number
     * @return a negative integer, zero, or a positive integer as this object's caller
     * number is less than, equal to, or greater than the specified object's caller number.
     */
    private int comparePhoneNumbers(String phoneNumberToBeCompared) {
        try {
            String thisCaller = this.callerNumber.replaceAll("-", "");
            String thatCaller = phoneNumberToBeCompared.replaceAll("-", "");
            Long thisNumber = Long.parseLong(thisCaller);
            Long thatNumber = Long.parseLong(thatCaller);

            if (thisNumber < thatNumber)
                return -1;
            else if (thisNumber > thatNumber)
                return 1;
            else
                return 0;
        } catch (NumberFormatException ex) {
            System.err.println("Something went wrong whilst attempting to parse the phone numbers");
            System.exit(1);
        }
        return 1;
    }

    /**
     * Creates and returns a <code>String</code> where all of the data
     * associated with the phone call has been nicely formatted.
     *
     * @return aesthetically pleasing phone call description
     */
    public String prettyPrint() {
        boolean displayOneDate = false;
        if (getJustDate(this.startTime).equals(getJustDate(this.endTime)))
            displayOneDate = true;
        String call = "\n  " + getJustDate(this.startTime);
        call = call.concat("\t");
        call = call.concat(this.callerNumber + "\t" + this.calleeNumber + "\t" + getJustTime(this.startTime));
        call = call.concat("\t\t");
        call = call.concat(getJustTime(this.endTime));
        call = call.concat("\t\t");
        call = call.concat(getCallDuration() + "\n");
        if (!displayOneDate)
            call = call.concat("  " + getJustDate(this.endTime) + "\n");
        return call;
    }

    /**
     * Get the date from some date and time.
     *
     * @param dateToParse some date and time
     * @return the date segment
     */
    private String getJustDate(String dateToParse) {
        String[] split = dateToParse.split(" ");
        Date date = null;
        DateFormat parseDate = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = parseDate.parse(dateToParse);
        } catch (ParseException ex) {
            System.err.println("Something went wrong whilst attempting to parse the date");
            System.exit(1);
        }
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    /**
     * Get the time from some date and time.
     *
     * @param dateToParse some date and time
     * @return the time segment
     */
    private String getJustTime(String dateToParse) {
        String[] split = dateToParse.split(" ");
        Date date = null;
        DateFormat parseDate = new SimpleDateFormat("hh:mm");
        try {
            date = parseDate.parse(split[1]);
        } catch (ParseException ex) {
            System.err.println("Something went wrong whilst attempting to parse the time");
            System.exit(1);
        }
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }
}
