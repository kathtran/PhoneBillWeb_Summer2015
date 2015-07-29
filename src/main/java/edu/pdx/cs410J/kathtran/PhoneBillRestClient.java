package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send key/value pairs.
 */
public class PhoneBillRestClient extends HttpRequestHelper {
    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

    private final String url;


    /**
     * Creates a client to the Phone Bill REST service running on the given host and port
     *
     * @param hostName The name of the host
     * @param port     The port
     */
    public PhoneBillRestClient(String hostName, int port) {
        this.url = String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET);
    }

    /**
     * Returns all customers and phone bills from the server
     */
    public Response getAllCustomersAndPhoneBills() throws IOException {
        return get(this.url);
    }

    /**
     * Returns all phone calls for the given customer
     */
    public Response getPhoneBills(String customer) throws IOException {
        return get(this.url, "customer", customer);
    }

    /**
     * Returns all phone calls between some given time specified by the user.
     *
     * @param startTime some time of day
     * @param endTime some time of day
     * @return phone calls that were started between the startTime and endTime times
     * @throws IOException
     */
    public Response getSearchedPhoneBills(String customer, String startTime, String endTime) throws IOException {
        return get(this.url, "customer", customer, "startTime", startTime, "endTime", endTime);
    }

    /**
     * Adds a phone call record to the phone bill of the specified customer.
     *
     * @param customer some name
     * @param phoneCall contains the record of some phone call
     * @return phone calls within the phone bill of the specified name
     * @throws IOException
     */
    public Response addCustomerPhoneCallPair(String customer, PhoneCall phoneCall) throws IOException {
        return post(this.url, "customer", customer, "phoneCall", phoneCall.toString());
    }
}
