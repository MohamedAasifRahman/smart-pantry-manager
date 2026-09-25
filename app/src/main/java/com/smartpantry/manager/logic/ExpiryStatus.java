package com.smartpantry.manager.logic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smartpantry.manager.util.DateUtils;


// Works out what an ingredient's expiry date should say on its pantry card

public final class ExpiryStatus {

    public enum State {
        NONE,       // nothing to show
        EXPIRED,    // the date has already passed
        TODAY,      // the date is today
        SOON        // within the number of days chosen in Settings
    }

    private final State state;
    private final int days;

    private ExpiryStatus(@NonNull State state, int days) {
        this.state = state;
        this.days = days;
    }

    // isoDate is often null: most pantry items have no expiry date at all.
    @NonNull
    public static ExpiryStatus evaluate(@Nullable String isoDate,
                                        boolean alertsEnabled,
                                        int thresholdDays) {
        // The Settings switch hides every badge, including expired ones.
        if (!alertsEnabled) {
            return new ExpiryStatus(State.NONE, 0);
        }

        Integer daysUntil = DateUtils.daysUntil(isoDate);
        if (daysUntil == null) {
            return new ExpiryStatus(State.NONE, 0);
        }
        if (daysUntil < 0) {
            return new ExpiryStatus(State.EXPIRED, daysUntil);
        }
        if (daysUntil == 0) {
            return new ExpiryStatus(State.TODAY, 0);
        }
        if (daysUntil <= thresholdDays) {
            return new ExpiryStatus(State.SOON, daysUntil);
        }
        // Further away than the user asked to be warned about.
        return new ExpiryStatus(State.NONE, daysUntil);
    }

    @NonNull
    public State getState() {
        return state;
    }

    // Whole days from today. Negative once the date has passed.
    public int getDays() {
        return days;
    }

    public boolean hasBadge() {
        return state != State.NONE;
    }
}
