package honorscltracker;

import java.util.*;

/**
 * A collection of comp learning activities for a single academic year.
 * It is assumed that the short description of activities will typically be the
 * name of the organization with which the activity was performed, so activities
 * are grouped by their short description to provide the most logical
 * organization.
 * @author Connor Pierce
 */
public class Year {
    private SortedSet<String> descs;
    private HashMap<String, ArrayList<CLActivity>> clActivities;
    private int startYr, size;
    
    /**
     * Creates a new <code>Year</code> object to represent the given academic
     * year.  The year given should be the start of the desired academic year;
     * for the 2012-13 academic year, for example, <code>year</code> should be
     * given as 2012.
     * @param year the starting year for the academic year; for the academic
     * year 2012-2013, this should be <code>2012</code>, for example
     */
    public Year(int year) {
        startYr = year;
        descs = new TreeSet<>();
        clActivities = new HashMap<>();
        size = 0;
    }
    
    /**
     * Adds a comp learning activity to this year. The activity is automatically
     * grouped with other activities of the same description, or if no
     * activities exist with the given description, this activity is placed in
     * its own group.
     * @param e the comp learning activity to be added
     */
    public void addCLActivity(CLActivity e) {
        String d = e.getDesc();
        descs.add(e.getDesc());
        if(clActivities.containsKey(d)) {
            clActivities.get(d).add(e);
        } else {
            ArrayList<CLActivity> a = new ArrayList<>();
            a.add(e);
            clActivities.put(d, a);
        }
        size++;
    }
    
    /**
     * Returns a string representing this academic year, in the format 
     * <code>"XXXX-XX"</code>.
     * @return string representation of this academic year, in the format
     * <code>"XXXX-XX"</code>
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
     * Removes the given comp learning activity from this year. If this year did
     * not contain the activity, returns false.  Otherwise, this method returns
     * true. If the given activity is the only activity with that description,
     * the activity is removed, and the description is removed from the list of
     * descriptions.
     * @param e the comp learning activity to remove
     * @return <code>true</code> if the activity was contained in this year,
     * <code>false</code> otherwise
     */
    public boolean removeCLActivity(CLActivity e) {
        ArrayList<CLActivity> activities = clActivities.get(e.getDesc());
        if(activities == null) {
            return false;
        } else {
            boolean result = activities.remove(e);
            if(result) { 
                //only decrement the size if a CLActivity was successfully
                //removed
                size--;
            }
            if(activities.size() < 1) {
                clActivities.remove(e.getDesc());
                descs.remove(e.getDesc());
            }
            return result;
        }
    }
    
    /**
     * Returns the number of comp learning activities performed during this
     * year.
     * @return the number of comp learning activities contained in this year
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Returns a list of all the different activity descriptions contained in
     * this year. Since comp learning activities are categorized according to
     * their description, this is the list of all the category names in this
     * year.
     * @return a list of all unique comp learning activity descriptions
     * contained within this list
     */
    public SortedSet<String> getAllDescs() {
        return descs;
    }

    /**
     * Determines whether this <code>Year</code> is equal to the given object.
     * If the given object is non-null, of type <code>Year</code>, and 
     * represents the same academic year as this <code>Year</code>, this method 
     * returns true.
     * @param obj the object to compare for equality
     * @return true if <code>obj</code> is non-null, of type <code>Year</code>,
     * and represents the same academic year as this <code>Year</code>
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Year other = (Year) obj;
        if (this.startYr != other.startYr) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.startYr;
        return hash;
    }
    
    /**
     * Returns all comp learning activities contained within this year that have
     * the given description.
     * @param desc the short description of the desired comp learning activities
     * @return the comp learning activities with the given description
     */
    public ArrayList<CLActivity> getCLActivities(String desc) {
        ArrayList<CLActivity> value = clActivities.get(desc);
        if(value == null) {
            return new ArrayList<>();
        } else {
            return value;
        }
    }

    /**
     * Gets the numerical value of this academic year. This is the year in which
     * the academic year began (<code>2012</code> for the 2012-2013 academic 
     * year, e.g).
     * @return the calendar year in which this academic year started 
     */
    public int getStartYear() {
        return startYr;
    }
    
}
