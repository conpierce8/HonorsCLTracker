package honorscltracker;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Stores comp learning activities.  Contains a list of <code>Year</code>s, and
 * contains methods through which comp learning activities are directly added.
 * @author Connor Pierce
 */
public class YearList extends ArrayList<Year> {
    
    /**
     * Finds the <code>Year</code> to which the <code>CLActivity</code> belongs,
     * if that <code>Year</code> exists in the list, and adds the
     * <code>CLActivity</code> to it. If the <code>Year</code> is not contained
     * in the list, the proper <code>Year</code> is created, added to the list,
     * and the <code>CLActivity</code> is added.
     * @param j the <code>CLActivity</code> to be added.
     */
    public void addData(CLActivity j) {
        Year m = new Year(j.getStartYr());
        int idx = indexOf(m);
        if(idx < 0) {
            add(m);
        } else {
            m = get(idx);
        }
        m.addCLActivity(j);
    }
    
    /**
     * Returns the names of all the <code>Year</code>s contained in this list,
     * in the form of strings: "XXXX-XX".
     * @return set of all year names contained in this list
     */
    public TreeSet<String> getYearsList() {
        TreeSet<String> yearNames = new TreeSet<>();
        for(Year y : this) {
            yearNames.add(y.getYearString());
        }
        return yearNames;
    }
    
    /**
     * Convenience method for obtaining a given year
     * @param year the year to search for
     * @return the index of the <code>Year</code> representing the academic year
     * starting at <code>year</code>, or <code>-1</code> if this list does not
     * contain that year
     */
    public int indexOf(int year) {
        return indexOf(new Year(year));
    }
    
}
