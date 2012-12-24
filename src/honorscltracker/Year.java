/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

import java.util.*;

/**
 *
 * @author Connor
 */
public class Year {
    private SortedSet<String> descs;
    private HashMap<String, ArrayList<CLActivity>> clActivities;
    private int startYr, size;
    
    public Year(int year) {
        startYr = year;
        descs = new TreeSet<>();
        clActivities = new HashMap<>();
        size = 0;
    }
    
    public void addCLActivity(CLActivity e) {
//        System.out.println("Adding date "+d.year+"/"+d.month+"/"+d.day);
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
    
    public String getYearString() {
        return startYr+"-"+(startYr % 100);
    }
    
    public boolean removeCLActivity(CLActivity e) {
        ArrayList<CLActivity> activities = clActivities.get(e.getDesc());
        if(activities == null) {
            return false;
        } else {
            size --;
            return activities.remove(e);
        }
    }
    
    public int getSize() {
        return size;
    }
    
    public SortedSet<String> getAllDescs() {
        return descs;
    }

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
    
    public ArrayList<CLActivity> getCLActivities(String desc) {
        ArrayList<CLActivity> value = clActivities.get(desc);
        if(value == null) {
            return new ArrayList<>();
        } else {
            return value;
        }
    }
    
}
