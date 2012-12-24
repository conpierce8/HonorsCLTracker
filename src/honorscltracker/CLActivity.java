/*
 * Copyright 2012, Connor D. Pierce
 */
package honorscltracker;

import java.util.GregorianCalendar;

/**
 *
 * @author Connor
 */
public class CLActivity implements Comparable<CLActivity> {
    
    private GregorianCalendar date;

    /**
     * Get the value of date
     *
     * @return the value of date
     */
    public GregorianCalendar getDate() {
        return date;
    }

    /**
     * Set the value of date
     *
     * @param date new value of date
     */
    public void setDate(GregorianCalendar date) {
        this.date = date;
    }
    
    private Contact contact;

    /**
     * Get the value of contact
     *
     * @return the value of contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Set the value of contact
     *
     * @param contact new value of contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    private double hours;

    /**
     * Get the value of hours
     *
     * @return the value of hours
     */
    public double getHours() {
        return hours;
    }

    /**
     * Set the value of hours
     *
     * @param hours new value of hours
     */
    public void setHours(double hours) {
        this.hours = hours;
    }

    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(CLActivity o) {
        int a = this.desc.compareTo(o.desc);
        if(a == 0) {
            return this.date.compareTo(o.date);
        } else {
            return a;
        }
    }

    private String desc;

    /**
     * Get the value of desc
     *
     * @return the value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Set the value of desc
     *
     * @param desc new value of desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    private String details;

    /**
     * Get the value of details
     *
     * @return the value of details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Set the value of details
     *
     * @param details new value of details
     */
    public void setDetails(String details) {
        this.details = details;
    }
    
}
