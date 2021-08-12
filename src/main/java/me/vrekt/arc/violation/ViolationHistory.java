package me.vrekt.arc.violation;

import me.vrekt.arc.check.CheckType;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of player violations
 */
public final class ViolationHistory {

    /**
     * Keeps track of violations by check type.
     */
    private final Map<CheckType, Integer> violations = new HashMap<>();

    /**
     * Get the violation level for a check
     *
     * @param check the check
     * @return the level
     */
    public int getViolationLevel(CheckType check) {
        return violations.getOrDefault(check, 0);
    }

    /**
     * Increment the violation level
     *
     * @param check the check
     */
    public int incrementViolationLevel(CheckType check) {
        final int level = getViolationLevel(check) + 1;
        violations.put(check, level);
        return level;
    }

    /**
     * @return violations history.
     */
    public Map<CheckType, Integer> getViolations() {
        return violations;
    }

    /**
     * Decrease the violation level by 1.
     *
     * @param check the check
     */
    public void decreaseViolationLevel(CheckType check) {
        violations.put(check, getViolationLevel(check) - 1);
    }

}
