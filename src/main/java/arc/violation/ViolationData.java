package arc.violation;

import arc.check.CheckType;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds data about violations
 */
public final class ViolationData {

    /**
     * The violation history for each check
     */
    private final Map<CheckType, Integer> violationHistory = new HashMap<>();

    /**
     * @param check the check.
     * @return how many times the player has failed the check.
     */
    public int getViolationLevel(CheckType check) {
        return violationHistory.getOrDefault(check, 0);
    }

    /**
     * Increment the violation level.
     *
     * @param check the check.
     */
    public void incrementViolationLevel(CheckType check) {
        violationHistory.put(check, getViolationLevel(check) + 1);
    }

    /**
     * @return {@code true} if this data has any violations
     */
    public boolean hasAnyViolations() {
        return !violationHistory.isEmpty();
    }

}
