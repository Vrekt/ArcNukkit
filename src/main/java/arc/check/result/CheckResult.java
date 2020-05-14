package arc.check.result;

/**
 * Represents the result of a check.
 */
public final class CheckResult {

    /**
     * Represents a success.
     */
    public static CheckResult PASSED = new CheckResult(false);

    /**
     * Represents a failure.
     */
    public static CheckResult FAILED = new CheckResult(true);

    /**
     * Default constructor
     *
     * @param failed {@code true} if failed
     */
    public CheckResult(boolean failed) {
        this.failed = failed;
    }

    /**
     * @param failed if failed
     * @param action the action to take
     */
    public CheckResult(boolean failed, CancelAction action) {
        this.failed = failed;
        this.action = action;
    }

    /**
     * If the check has failed.
     */
    private boolean failed;
    /**
     * The action to take.
     */
    private CancelAction action;

    /**
     * @return if the check failed.
     */
    public boolean failed() {
        return failed;
    }

    /**
     * @return {@code true} if the event should be cancelled.
     */
    public boolean cancel() {
        return action != null && action == CancelAction.CANCEL;
    }

    /**
     * @return the action
     */
    public CancelAction action() {
        return action;
    }
}
