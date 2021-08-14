package me.vrekt.arc.exemption;

import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.exemption.type.ExemptionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of a players exemptions
 */
public final class ExemptionHistory {

    /**
     * Empty
     */
    public static final ExemptionHistory EMPTY = new ExemptionHistory();

    /**
     * Exemption data
     */
    private final Map<CheckType, Long> exemptions = new HashMap<>();

    /**
     * Set of exemption types
     */
    private final Map<ExemptionType, Long> exemptionTypes = new HashMap<>();

    /**
     * Add an exemption
     *
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(CheckType check, long duration) {
        exemptions.put(check, duration);
    }

    /**
     * Add an exemption permanently
     *
     * @param check the check
     */
    public void addExemptionPermanently(CheckType check) {
        exemptions.put(check, -1L);
    }

    /**
     * Add an exemption type
     *
     * @param type the type
     */
    public void addExemption(ExemptionType type) {
        exemptionTypes.put(type, -1L);
    }


    /**
     * Add an exemption type
     *
     * @param type     the type
     * @param duration the duration
     */
    public void addExemption(ExemptionType type, long duration) {
        exemptionTypes.put(type, duration);
    }

    /**
     * Remove an exemption type
     *
     * @param type the type
     */
    public void removeExemption(ExemptionType type) {
        exemptionTypes.remove(type);
    }

    /**
     * Check if there is an exemption
     *
     * @param check the check
     * @return {@code true} if so
     */
    public boolean isExempt(CheckType check) {
        final long time = exemptions.getOrDefault(check, 0L);
        if (time == 0) return false;
        // perm exemption
        if (time == -1) return true;

        final boolean result = (time - System.currentTimeMillis() <= 0);
        if (result) exemptions.remove(check);
        return result;
    }

    /**
     * Check if there is an exemption
     *
     * @param type the type
     * @return {@code true} if so
     */
    public boolean isExempt(ExemptionType type) {
        final long time = exemptionTypes.getOrDefault(type, 0L);
        if (time == 0) return false;
        if (time == -1) return true;

        final boolean result = (time - System.currentTimeMillis() <= 0);

        if (result) exemptionTypes.remove(type);
        return !result;
    }

    /**
     * Clear these exemptions
     */
    public void clear() {
        exemptions.clear();
        exemptionTypes.clear();
    }

}
