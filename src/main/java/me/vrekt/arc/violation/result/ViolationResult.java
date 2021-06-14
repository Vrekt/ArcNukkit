package me.vrekt.arc.violation.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a violation result
 */
public final class ViolationResult {

    /**
     * Empty/default result.
     */
    public static final ViolationResult EMPTY = new ViolationResult();

    /**
     * The result
     */
    public enum Result {
        CANCEL, BAN, KICK, NOTIFY
    }

    /**
     * List of results
     */
    private final List<Result> results = new ArrayList<>();

    /**
     * Add a result
     *
     * @param result the result
     */
    public void addResult(Result result) {
        results.add(result);
    }

    /**
     * If we should cancel
     * {@code true} if so
     */
    public boolean cancel() {
        return results.contains(Result.CANCEL);
    }

    /**
     * If we should ban
     * {@code true} if so
     */
    public boolean ban() {
        return results.contains(Result.BAN);
    }

    /**
     * If we should kick
     * {@code true} if so
     */
    public boolean kick() {
        return results.contains(Result.KICK);
    }

    /**
     * If we should notify
     * {@code true} if so
     */
    public boolean notifyViolation() {
        return results.contains(Result.NOTIFY);
    }

    /**
     * If we should do nothing.
     *
     * @return {@code true} if so
     */
    public boolean doNothing() {
        return results.isEmpty();
    }

}
