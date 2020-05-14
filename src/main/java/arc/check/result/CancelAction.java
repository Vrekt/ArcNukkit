package arc.check.result;

/**
 * The action to take to cancel.
 */
public enum CancelAction {

    /**
     * Set the player back to the original location.
     */
    SETBACK,

    /**
     * Set the player back to a specific location
     */
    SETBACK_TO,

    /**
     * Cancel the event
     */
    CANCEL,

    /**
     * Do nothing.
     */
    NONE;

}
