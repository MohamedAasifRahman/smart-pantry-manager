package com.smartpantry.manager.model;

import androidx.annotation.NonNull;

/**
 * One item the user currently has at home, i.e. one row of the {@code pantry} table.
 *
 * <p>The expiry date is optional. It is stored as an ISO {@code yyyy-MM-dd} string
 * because SQLite has no dedicated date type, and dates in that format sort and
 * compare correctly as plain text.</p>
 */
public class Ingredient {

    /** Id used for an ingredient that has not been saved to the database yet. */
    public static final long NO_ID = -1L;

    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate;

    /** Creates an unsaved ingredient. */
    public Ingredient(String name, double quantity, String unit, String expiryDate) {
        this(NO_ID, name, quantity, unit, expiryDate);
    }

    /** Creates an ingredient that already exists in the database. */
    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /** @return the expiry date as {@code yyyy-MM-dd}, or {@code null} when none was given. */
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /** @return true when this ingredient has not been written to the database yet. */
    public boolean isNew() {
        return id == NO_ID;
    }

    public boolean hasExpiryDate() {
        return expiryDate != null && !expiryDate.trim().isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + quantity + " " + unit;
    }
}
