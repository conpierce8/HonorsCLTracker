package honorscltracker;

/**
 * Represents a user action, such as adding/deleting/editing a CLActivity.  Used
 * to remember an undo/redo sequence.
 * @author Connor Pierce
 */
public class UserAction {
    /**
     * Indicates that the <code>UserAction</code> represents the addition of a
     * new comp learning activity.
     */
    public static final int ADDED=0;
    /**
     * Indicates that the <code>UserAction</code> represents the deletion of a
     * comp learning activity.
     */
    public static final int DELETED=1;
    /**
     * Indicates that the <code>UserAction</code> represents the editing of the
     * details of a comp learning activity.
     */
    public static final int EDITED=2;
    private int type;
    private CLActivity previous;
    private CLActivity current;
    private boolean saved = false;
    
    /*
     * Private constructor for UserActions.  Declared private to avoid
     * confusion about usage---use static initialization methods
     */
    private UserAction(int type, CLActivity current, CLActivity previous) {
        if(type < 0 || type > 2) {
            throw new IllegalArgumentException("Invalid type specified");
        }
        this.type = type;
        this.current = current;
        this.previous = previous;
    }
    
    /**
     * Creates a UserAction representing the addition of a new comp learning
     * activity.
     * @param added the newly created CLActivity
     * @return a <code>UserAction</code> with <code>type=ADDED</code>,
     * <code>current=added</code>, <code>previous=null</code>
     */
    public static UserAction addition(CLActivity added) {
        return new UserAction(ADDED, added, null);
    }
    
    public static UserAction deletion(CLActivity deleted) {
        return new UserAction(DELETED, null, deleted);
    }
    
    public static UserAction edit(CLActivity old, CLActivity updated) {
        return new UserAction(EDITED, updated, old);
    }

    /**
     * Get the value of current--if the <code>UserAction</code> represents an
     * addition, <code>current</code> contains the <code>CLActivity</code> that
     * was added.  If the <code>UserAction</code> represents a deletion,
     * <code>current</code> is <code>null</code>. If the <code>UserAction</code>
     * represents an edit, <code>current</code> contains the 
     * <code>CLActivity</code> that was reflects the new data.
     *
     * @return the value of current
     */
    public CLActivity getCurrent() {
        return current;
    }

    /**
     * Get the value of previous--if the <code>UserAction</code> represents an
     * addition, <code>previous</code> is null.  If the <code>UserAction</code> 
     * represents a deletion,<code>previous</code> contains the 
     * <code>CLActivity</code> that was removed. If the <code>UserAction</code>
     * represents an edit, <code>previous</code> contains the 
     * <code>CLActivity</code> that was reflects the data prior to editing.
     *
     * @return the value of previous
     */
    public CLActivity getPrevious() {
        return previous;
    }

    /**
     * Get the value of type--<code>ADDED</code>, <code>DELETED</code>,
     * <code>EDITED</code>.
     *
     * @return the value of type
     */
    public int getType() {
        return type;
    }
    
    public void save() {
        saved = true;
    }
    
    public void unsave() {
        saved = false;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
}
