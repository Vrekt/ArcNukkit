package arc.check.management;

import arc.check.CheckConfigBase;
import arc.check.CheckType;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of check configurations
 */
public final class Checks {

    /**
     * The cache.
     */
    private static final Map<CheckType, CheckConfigBase> CHECK_CACHE = new HashMap<>();

    /**
     * Register a check.
     *
     * @param check the check
     * @param base  the base
     */
    public static void register(CheckType check, CheckConfigBase base) {
        CHECK_CACHE.put(check, base);
    }

    /**
     * Check if a check is enabled.
     *
     * @param check the check
     * @return {@code true} if so.
     */
    public static boolean isEnabled(CheckType check) {
        return CHECK_CACHE.get(check).enabled();
    }

}
