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
public class PhoneBillServlet extends HttpServlet
{
    private final Map<String, PhoneBill> data = new HashMap<>();

    /**
     * Handles an HTTP GET request from a client by writing the value of the key
     * specified in the "key" HTTP parameter to the HTTP response.  If the "key"
     * parameter is not specified, all of the key/value pairs are written to the
     * HTTP response.
     *
     * @param request data from the client
     * @param response data returned to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );

        String customer = getParameter( "customer", request );
        String caller = getParameter("caller", request);
        String callee = getParameter("caller", request);
        String start = getParameter("caller", request);
        String end = getParameter( "caller", request );

        if (customer != null && caller != null && callee != null && start != null && end != null) {
            writeValue(customer, response);      // If customer IS specified, display only their call records
        } else {
            writeAllMappings(response);     // If no customer is specified, display all customer/call records
        }
    }

    /**
     * Handles an HTTP POST request by storing the key/value pair specified by the
     * "key" and "value" request parameters.  It writes the key/value pair to the
     * HTTP response.
     *
     * @param request data from the client
     * @param response data returned to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );

        String customer = getParameter( "customer", request );
        if (customer == null) {
            missingRequiredParameter( response, "customer" );       // If customer isn't supplied
            return;
        }

        String phoneCall = getParameter("phonecall", request);
        if ( phoneCall == null) {
            missingRequiredParameter(response, "phonecall");          // If no phonecall records
            return;
        }

        PhoneCall phoneCallToAdd = new PhoneCall(phoneCall);
        PhoneBill phoneBill = this.data.get(customer);

        if (phoneBill == null)
            phoneBill = new PhoneBill(customer);
        phoneBill.addPhoneCall(phoneCallToAdd);
        this.data.put(customer, phoneBill);

        PrintWriter pw = response.getWriter();
        pw.println(Messages.mappedCustomerPhoneBill(customer, phoneBill));
        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        PrintWriter pw = response.getWriter();
        pw.println( Messages.missingRequiredParameter(parameterName));
        pw.flush();
        
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
    }

    /**
     * Writes the value of the given customer to the HTTP response.
     *
     * The text of the message is formatted with {@link Messages#getMappingCount(int)}
     * and {@link Messages#formatCustomerPhoneBillPair(String, PhoneBill)}
     */
    private void writeValue( String customer, HttpServletResponse response ) throws IOException
    {
        PhoneBill phonebill = this.data.get(customer);

        PrintWriter pw = response.getWriter();
        pw.println(Messages.getMappingCount( phonebill != null ? 1 : 0 ));
        pw.println(Messages.formatCustomerPhoneBillPair(customer, phonebill));

        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Prints out all phone calls that correspond to the specified customer.
     *
     * @param customer
     * @param response
     * @throws IOException
     */
    private void writeSpecifiedValue ( String customer, HttpServletResponse response ) throws IOException
    {
        PrintWriter pw = response.getWriter();
        this.data.entrySet().stream().filter(entry -> entry.getKey().contentEquals(customer)).forEach(entry -> {
            pw.println(entry.getKey());
            for (Object phoneCall : entry.getValue().getPhoneCalls()) {
                PhoneCall phoneCallToAdd = (PhoneCall) phoneCall;
                pw.println(phoneCallToAdd.prettyPrint());
            }
        });
    }

    /**
     * Writes all of the key/value pairs to the HTTP response.
     *
     * The text of the message is formatted with
     * {@link Messages#formatKeyValuePair(String, String)}
     */
    private void writeAllMappings( HttpServletResponse response ) throws IOException
    {
        PrintWriter pw = response.getWriter();
        pw.println(Messages.getMappingCount(data.size()));

        for (Map.Entry<String, PhoneBill> entry : this.data.entrySet()) {
            pw.println(Messages.formatCustomerPhoneBillPair(entry.getKey(), entry.getValue()));
        }

        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;
      } else {
        return value;
      }
    }

    public Map<String, PhoneBill> getData() {
        return data;
    }
}
