package honorscltracker;

import java.util.GregorianCalendar;

/**
 * Class describing a single Complementary Learning activity on a single day at
 * a single location.  Includes the date and description (usually the
 * organization for/with which the activity was performed) of the event, a
 * detailed description of what the student did, contact information for the
 * individual in charge of the event, and the academic year to which the event
 * belongs.
 * @author Connor Pierce
 */
public class CLActivity implements Comparable<CLActivity> {
    
    private GregorianCalendar date;

    /**
     * Get the date on which the activity was performed.
     *
     * @return the the on which the activity was performed
     */
    public GregorianCalendar getDate() {
        return date;
    }
    
    private int startYr;

    /**
     * Get the first value of the academic year in which the activity was
     * performed.  For example, if the activity was performed for the 2012-13
     * academic year, this method returns <code>2012</code>.
     *
     * @return the academic year in which the activity was performed
     */
    public int getStartYr() {
        return startYr;
    }

    /**
     * Sets the academic year that the activity was performed, using the first
     * calendar year of that year. For example, for a comp learning activity
     * performed during the 2012-13 academic year, this method should be called
     * with <code>2012</code> passed to <code>startYr</code>
     *
     * @param startYr year the comp learning activity was performed
     */
    public void setStartYr(int startYr) {
        this.startYr = startYr;
    }
    
    /**
     * Gets the academic year in which the comp learning activity was performed,
     * as a string formatted as <code>"XXXX-XX"</code>.  Used for getting the
     * display value of the academic year.
     * 
     * @return a <code>String</code> of the form <code>"XXXX-XX"</code>,
     * signifying the academic year in which the comp learing activity was
     * performed
     */
    public String getYearString() {
        String s = startYr + "-";
        if((startYr + 1) % 100 < 10) {
            s += "0";
        } 
        s += ((startYr + 1) % 100);
        return s;
    }

    /**
     * Sets the date on which the comp learning activity was performed.
     *
     * @param date the date the comp learning activity was performed on
     */
    public void setDate(GregorianCalendar date) {
        this.date = date;
    }
    
    private Contact contact;

    /**
     * Gets the contact details for a person who can verify that the comp
     * learning activity was performed as described.
     *
     * @return contact details for the person in charge of the activity
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the contact details for the person in charge of this activity.
     *
     * @param contact contact details for the person in charge of the comp
     * learning activity
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    private double hours;

    /**
     * Gets the number of hours that the student spent at this comp learning
     * activity.
     *
     * @return the hours spent on this comp learning activity
     */
    public double getHours() {
        return hours;
    }

    /**
     * Sets the number of hours that the student spent on this comp learning
     * activity.
     *
     * @param hours hours spent on this comp learning activity
     */
    public void setHours(double hours) {
        this.hours = hours;
    }

    /**
     * Compares two comp learning activities, based first on an alphabetic
     * comparison of their primary descriptions, then based on their dates if
     * the descriptions are identical. Descriptions are compared using 
     * <code>String.compareTo(String)</code>, and dates are compared using
     * <code>GregorianCalendar.compareTo(GregorianCalendar)</code>
     * 
     * @param o the <code>CLActivity</code> to which this activity is to be
     * compared
     * @return a positive integer if this activity's description is 
     * alphabetically after <code>o</code>'s description, or if these activities
     * have the same description and this activity's date falls chronologically 
     * after <code>o</code>'s date; 0 if these activities have identical dates
     * and descriptions; a negative integer if this activity's description is
     * alphabetically before <code>o</code>'s description, or if the
     * descriptions are equal and this activity's date is chronologically before
     * <code>o</code>'s date
     * @see String#compareTo(String)
     * @see java.util.Calendar#compareTo(java.util.Calendar)
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
     * Gets the short description (organization name, e.g.) of this comp
     * learning activity.
     *
     * @return the short description of this activity
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the short description (organization name, e.g.) of this activity.
     *
     * @param desc the short description of this activity
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    private String details;

    /**
     * Gets the detailed description of this activity.  This is typically an
     * HTML-formatted string that describes in detail the role the student
     * played in the activity.
     *
     * @return the detailed description of the activity
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the detailed description of the activity. This is typically an
     * HTML-formatted string that describes in detail the student's role in the
     * activity.
     *
     * @param details the detailed description of the activity
     */
    public void setDetails(String details) {
        this.details = details;
    }
    
}
