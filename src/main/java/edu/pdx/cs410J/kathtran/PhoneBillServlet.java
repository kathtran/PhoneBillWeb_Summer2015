package edu.pdx.cs410J.kathtran;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>PhoneBill</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple key/value pairs.
 */
public class PhoneBillServlet extends HttpServlet {
    private final Map<String, PhoneBill> data = new HashMap<>();
    private static PrintWriter pw;

    /**
     * Handles an HTTP GET request from a client by writing the value of the key
     * specified in the "key" HTTP parameter to the HTTP response.  If the "key"
     * parameter is not specified, all of the key/value pairs are written to the
     * HTTP response.
     *
     * @param request  data from the client
     * @param response data returned to the client
     * @throws ServletException some servlet error
     * @throws IOException      some IO error
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        String customer = getParameter("customer", request);
        String searchAfter = getParameter("searchAfter", request);
        String searchBefore = getParameter("searchBefore", request);

        if (customer != null && searchAfter == null && searchBefore == null)
            writePhoneBill(customer, response);
        else if (customer != null && searchAfter != null && searchBefore != null)
            writeTimeSpecifiedPhoneBill(customer, searchAfter, searchBefore, response);
        else
            writeAllMappings(response);
    }

    /**
     * Handles an HTTP POST request by storing the customer/phone bill pair specified by the
     * "customer" and "phoneCall" request parameters.  It writes the customer/phone bill pair
     * to the HTTP response.
     *
     * @param request  data from the client
     * @param response data returned to the client
     * @throws ServletException some servlet error
     * @throws IOException      some IO error
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        String customer = getParameter("customer", request);
        if (customer == null) {
            missingRequiredParameter(response, "customer");
            return;
        }

        String phoneCall = getParameter("phoneCall", request);
        if (phoneCall == null) {
            missingRequiredParameter(response, "phoneCall");
            return;
        }

        PhoneCall phoneCallToAdd = new PhoneCall(phoneCall);
        PhoneBill phoneBill = this.data.get(customer);
        if (phoneBill == null)
            phoneBill = new PhoneBill(customer);
        phoneBill.addPhoneCall(phoneCallToAdd);
        this.data.put(customer, phoneBill);

        pw = response.getWriter();
        pw.println(Messages.mappedCustomerPhoneBill(customer, phoneBill));

        pw.flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     *
     * @param response      data returned to the client
     * @param parameterName some parameter
     * @throws IOException some IO error
     */
    private void missingRequiredParameter(HttpServletResponse response, String parameterName) throws IOException {
        pw = response.getWriter();
        pw.println(Messages.missingRequiredParameter(parameterName));

        pw.flush();
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
    }

    /**
     * Writes the phone bill of the given customer to the HTTP response.
     * <p>
     * The text of the message is formatted with {@link Messages#getMappingCount(int)}
     * and {@link Messages#formatCustomerPhoneBillPair(String, PhoneBill)}
     *
     * @param customer some name
     * @param response data returned to the client
     * @throws IOException some IO error
     */
    private void writePhoneBill(String customer, HttpServletResponse response) throws IOException {
        PhoneBill phoneBill = this.data.get(customer);

        pw = response.getWriter();
        pw.println(Messages.getMappingCount(phoneBill != null ? 1 : 0));
        pw.println(Messages.formatCustomerPhoneBillPair(customer, phoneBill));

        pw.flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Prints out all phone calls belonging to the specified customer
     * that began between some time between the two specified times.
     *
     * @param customer  some name
     * @param startTime the lower bound time
     * @param endTime   the upper bound time
     * @param response  data returned to the client
     * @throws IOException some IO error
     */
    private void writeTimeSpecifiedPhoneBill(String customer, String startTime, String endTime, HttpServletResponse response)
            throws IOException {
        pw = response.getWriter();
        this.data.entrySet().stream().filter(entry -> entry.getKey().equals(customer)).forEach(entry -> {
            pw.println(entry.getKey());
            for (Object phoneCall : entry.getValue().getPhoneCalls()) {
                PhoneCall call = (PhoneCall) phoneCall;
                String callStart = call.getStartTimeString();
                if ((callStart.compareTo(startTime) == 0 || callStart.compareTo(startTime) == 1) &&
                        (callStart.compareTo(endTime) == 0 || callStart.compareTo(endTime) == -1))
                    pw.println(call.prettyPrint());
            }
        });
        pw.flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Writes all of the customer/phone bill pairs to the HTTP response.
     * <p>
     * The text of the message is formatted with
     * {@link Messages#formatCustomerPhoneBillPair(String, PhoneBill)}
     *
     * @param response data returned to the client
     * @throws IOException some IO error
     */
    private void writeAllMappings(HttpServletResponse response) throws IOException {
        pw = response.getWriter();
        pw.println(Messages.getMappingCount(data.size()));

        this.data.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry ->
                pw.println(Messages.formatCustomerPhoneBillPair(entry.getKey(), entry.getValue())));

        pw.flush();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @param name    some name
     * @param request data from the client
     * @return <code>null</code> if the value of the parameter is
     * <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || "".equals(value)) {
            return null;
        } else {
            return value;
        }
    }
}
