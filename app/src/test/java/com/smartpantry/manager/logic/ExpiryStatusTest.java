package com.smartpantry.manager.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.smartpantry.manager.util.DateUtils;

import org.junit.Test;

import java.util.Calendar;

// Checks the rule that decides which badge a pantry card shows.

public class ExpiryStatusTest {

    private static final boolean ALERTS_ON = true;
    private static final boolean ALERTS_OFF = false;
    private static final int THRESHOLD = 3;

    private static String inDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return DateUtils.format(calendar);
    }

    @Test
    public void noExpiryDateShowsNoBadge() {
        ExpiryStatus status = ExpiryStatus.evaluate(null, ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.NONE, status.getState());
        assertFalse(status.hasBadge());
    }

    @Test
    public void unreadableDateShowsNoBadge() {
        ExpiryStatus status = ExpiryStatus.evaluate("not a date", ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.NONE, status.getState());
    }

    @Test
    public void alertsOffHidesEvenAnExpiredItem() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(-5), ALERTS_OFF, THRESHOLD);
        assertEquals(ExpiryStatus.State.NONE, status.getState());
        assertFalse(status.hasBadge());
    }

    @Test
    public void yesterdayIsExpired() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(-1), ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.EXPIRED, status.getState());
        assertEquals(-1, status.getDays());
        assertTrue(status.hasBadge());
    }

    @Test
    public void longPastDateIsStillExpired() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(-90), ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.EXPIRED, status.getState());
    }

    @Test
    public void todayIsItsOwnState() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(0), ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.TODAY, status.getState());
        assertEquals(0, status.getDays());
    }

    @Test
    public void insideTheThresholdIsExpiringSoon() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(2), ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.SOON, status.getState());
        assertEquals(2, status.getDays());
    }

    @Test
    public void exactlyOnTheThresholdIsExpiringSoon() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(THRESHOLD), ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.SOON, status.getState());
    }

    @Test
    public void oneDayPastTheThresholdShowsNoBadge() {
        ExpiryStatus status = ExpiryStatus.evaluate(inDays(THRESHOLD + 1), ALERTS_ON, THRESHOLD);
        assertEquals(ExpiryStatus.State.NONE, status.getState());
    }

    @Test
    public void aLargerThresholdCatchesMoreItems() {
        String sameDate = inDays(10);
        assertEquals(ExpiryStatus.State.NONE,
                ExpiryStatus.evaluate(sameDate, ALERTS_ON, 7).getState());
        assertEquals(ExpiryStatus.State.SOON,
                ExpiryStatus.evaluate(sameDate, ALERTS_ON, 14).getState());
    }
}
