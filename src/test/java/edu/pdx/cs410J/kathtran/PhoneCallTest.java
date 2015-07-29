package edu.pdx.cs410J.kathtran;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Vy on 7/28/2015.
 */
public class PhoneCallTest {
    private static PhoneCall phoneCall = new PhoneCall();

    @Test
    public void testGetDateObject() {
        Date given = phoneCall.getDateObject("6/5/2015 10:30 AM");
        Date searchAfter = phoneCall.getDateObject("1/1/2015 1:00 AM");
        Date searchBefore = phoneCall.getDateObject("12/12/2015 4:00 AM");

        assertTrue(given.after(searchAfter) && given.before(searchBefore));
    }

    @Test
    public void testCompareTime() {
        assertEquals(phoneCall.compareTime("6/5/2015 10:30 AM", "6/5/2015 10:30 AM"), 0);
        assertEquals(phoneCall.compareTime("6/5/2015 10:30 AM", "6/4/2015 10:30 AM"), 1);
        assertEquals(phoneCall.compareTime("6/5/2015 10:30 AM", "6/7/2015 10:30 AM"), -1);
    }
}