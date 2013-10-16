package honorscltracker;

/**
 * Defines a generic action handler, for communication between the screens and
 * the <code>Main</code> class.
 * @author Connor Pierce
 */
public abstract class Handler {
    
    /**
     * Method called when an action occurs that this <code>Handler</code> is
     * responsible for.  This could be clicking a button to switch between
     * screens, or adding a new comp learning event.
     * @param data any data that might be associated with the event--for
     * example, a newly created <code>CLActivity</code>
     */
    public abstract void action(Object data);
    
}
