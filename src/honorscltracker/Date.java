/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

/**
 *
 * @author Connor
 */
public class Date implements Comparable<Date> {
    public int year;
    public int month;
    public int day;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Date) {
            Date d = (Date) obj;
            return d.year == year && d.month == month && d.day == day;
        } else 
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.year;
        hash = 71 * hash + this.month;
        hash = 71 * hash + this.day;
        return hash;
    }

    @Override
    public int compareTo(Date o) {
        if(o.year == year) {
            if(o.month == month) {
                return day - o.day;
            } else {
                return month - o.month;
            }
        } else {
            return year - o.year;
        }
    }
    
}
