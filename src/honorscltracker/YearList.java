/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;

/**
 *
 * @author Connor
 */
public class YearList extends ArrayList<Year> {
    
    public void addData(CLActivity j) {
        Year m = new Year(j.getDate().get(Calendar.YEAR));
        int idx = indexOf(m);
        if(idx < 0) {
            add(m);
        } else {
            m = get(idx);
        }
        m.addCLActivity(j);
    }
    
    public TreeSet<String> getYearsList() {
        TreeSet<String> exerNames = new TreeSet<>();
        for(Year y : this) {
            exerNames.add(y.getYearString());
        }
        return exerNames;
    }
    
    public int indexOf(int year) {
        return indexOf(new Year(year));
    }
    
}
