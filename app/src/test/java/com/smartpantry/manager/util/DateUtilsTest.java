package com.smartpantry.manager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Calendar;

// Checks the date handling behind expiry dates: parsing, validation and the
// day arithmetic the expiry badges depend on.
public class DateUtilsTest {

    private static String inDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return DateUtils.format(calendar);
    }

    @Test
    public void parsesAWellFormedDate() {
        assertNotNull(DateUtils.parse("2026-09-26"));
    }

    @Test
    public void refusesNullAndBlankText() {
        assertNull(DateUtils.parse(null));
        assertNull(DateUtils.parse(""));
        assertNull(DateUtils.parse("   "));
    }

    @Test
    public void refusesTextThatIsNotADate() {
        assertNull(DateUtils.parse("tomorrow"));
        assertNull(DateUtils.parse("26/09/2026"));
    }

    @Test
    public void refusesImpossibleDatesInsteadOfRollingThemOver() {
        // A lenient parser would turn month 13 into January of the next year.
        assertNull(DateUtils.parse("2026-13-01"));
        assertNull(DateUtils.parse("2026-02-30"));
    }

    @Test
    public void surroundingSpacesAreIgnored() {
        assertNotNull(DateUtils.parse("  2026-09-26  "));
    }

    @Test
    public void validityAndRealismAreSeparateChecks() {
        // A real date, but far outside the range a pantry item could have.
        assertTrue(DateUtils.isValidDate("1899-01-01"));
        assertFalse(DateUtils.isRealisticExpiry("1899-01-01"));
        assertTrue(DateUtils.isRealisticExpiry("2026-09-26"));
    }

    @Test
    public void countsWholeDaysFromToday() {
        assertEquals(Integer.valueOf(0), DateUtils.daysUntil(inDays(0)));
        assertEquals(Integer.valueOf(1), DateUtils.daysUntil(inDays(1)));
        assertEquals(Integer.valueOf(-1), DateUtils.daysUntil(inDays(-1)));
        assertEquals(Integer.valueOf(30), DateUtils.daysUntil(inDays(30)));
    }

    @Test
    public void dayCountIsNullWhenThereIsNoUsableDate() {
        assertNull(DateUtils.daysUntil(null));
        assertNull(DateUtils.daysUntil("not a date"));
    }

    @Test
    public void displayFormatKeepsUnreadableTextAsItIs() {
        // The card should show whatever is stored rather than blank it out.
        assertEquals("not a date", DateUtils.formatForDisplay("not a date"));
        assertEquals("", DateUtils.formatForDisplay(null));
    }

    @Test
    public void displayFormatIsNotTheStoredFormat() {
        String displayed = DateUtils.formatForDisplay("2026-09-26");
        assertFalse(displayed.equals("2026-09-26"));
        assertTrue(displayed.contains("2026"));
        assertTrue(displayed.contains("26"));
    }
}
