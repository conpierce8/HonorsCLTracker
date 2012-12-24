/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

/**
 * Represents the units that an exercise is expressed in. Contains a conversion
 * factor, to standard units. Multiplying the conversion factor by the unit
 * gives the standard unit.
 * Standard units are as follows:
 * <ul>
 * <li>time - seconds</li>
 * <li>distance - meters</li>
 * </ul>
 * @author Connor
 */
public enum Unit {
    
    SEC("s", "seconds", 1, 1),
    MIN("min", "minutes", 1, 60),
    HOUR("hr", "hours", 1, 3600),
    
    FT("ft", "feet", 1, 0.3048),
    IN("in", "inches", 1, 0.0254),
    M("m", "meters", 1, 1),
    KM("km", "kilometers", 1, 1000),
    MILE("mi", "miles", 1, 1609.344),
    YD("s", "seconds", 1, .9144);
    
    private final String abbrev;
    private final String fullName;
    private final int type;
    private final double conversion;
    private static final int dist = 0, time = 1, other = 2;
    
    private Unit(String abbrev, String name, int type, double conversion) {
        this.abbrev = abbrev;
        fullName = name;
        this.type = type;
        this.conversion = conversion;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public double getConversion() {
        return conversion;
    }

    public String getFullName() {
        return fullName;
    }

    public int getType() {
        return type;
    }
    
}
