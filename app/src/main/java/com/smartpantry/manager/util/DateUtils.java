package com.smartpantry.manager.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Parsing, formatting and day arithmetic for expiry dates.

public final class DateUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final int EARLIEST_YEAR = 2000;
    private static final int LATEST_YEAR = 2100;
    private static final long MILLIS_PER_DAY = 24L * 60L * 60L * 1000L;

    private DateUtils() {
    }

    @NonNull
    public static String format(@NonNull Calendar calendar) {
        return createFormat().format(calendar.getTime());
    }

    // Returns null when the text is not a valid date.
    @Nullable
    public static Date parse(@Nullable String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return null;
        }
        try {
            return createFormat().parse(isoDate.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isValidDate(@Nullable String isoDate) {
        return parse(isoDate) != null;
    }

    // Guards against typing mistakes such as the year 220 or 20260.
    public static boolean isRealisticExpiry(@Nullable String isoDate) {
        Date date = parse(isoDate);
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year >= EARLIEST_YEAR && year <= LATEST_YEAR;
    }

    // Whole days from today: 0 for today, negative once the date has passed,null when the text is not a valid date.
    @Nullable
    public static Integer daysUntil(@Nullable String isoDate) {
        Date date = parse(isoDate);
        if (date == null) {
            return null;
        }
        long target = startOfDay(date).getTimeInMillis();
        long today = startOfDay(new Date()).getTimeInMillis();
        // Rounding rather than truncating keeps this correct across a daylight

        return (int) Math.round((target - today) / (double) MILLIS_PER_DAY);
    }

    @NonNull
    public static Calendar startOfDay(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    // Not lenient, so "2026-13-45" is rejected instead of rolling into another date.
    private static SimpleDateFormat createFormat() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        format.setLenient(false);
        return format;
    }
}
