package edu.pdx.cs410J.kathtran;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests the {@link Project4} class by invoking its main method with various arguments 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Project4Test extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");

    @Test
    public void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getErr(), containsString(Project4.MISSING_ARGS));
    }

    @Test
    public void test2EmptyServer() {
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT );
        assertThat(result.getErr(), result.getExitCode(), equalTo(0));
        String out = result.getOut();
        assertThat(out, out, containsString(Messages.getMappingCount(0)));
    }

    @Test
    public void test3NoValues() {
        String key = "KEY";
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, key );
        assertThat(result.getErr(), result.getExitCode(), equalTo(0));
        String out = result.getOut();
        assertThat(out, out, containsString(Messages.getMappingCount(0)));
        assertThat(out, out, containsString(Messages.formatCustomerPhoneBillPair(key, null)));
    }

//    @Test
//    @Ignore
//    public void test4AddValue() {
//        String key = "KEY";
//        String value = "VALUE";
//
//        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, key, value );
//        assertThat(result.getErr(), result.getExitCode(), equalTo(0));
//        String out = result.getOut();
//        assertThat(out, out, containsString(Messages.mappedCustomerPhoneBill(key, value)));
//
//        result = invokeMain( Project4.class, HOSTNAME, PORT, key );
//        out = result.getOut();
//        assertThat(out, out, containsString(Messages.getMappingCount(1)));
//        assertThat(out, out, containsString(Messages.formatCustomerPhoneBillPair(key, value)));
//
//        result = invokeMain( Project4.class, HOSTNAME, PORT );
//        out = result.getOut();
//        assertThat(out, out, containsString(Messages.getMappingCount(1)));
//        assertThat(out, out, containsString(Messages.formatCustomerPhoneBillPair(key, value)));
//    }
}