package me.vrekt.arc.check;

import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.check.player.FastUse;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;

import java.io.Closeable;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A check manager
 */
public final class CheckManager extends Configurable implements Closeable {

    /**
     * All the checks
     */
    private final Set<Check> checks = new HashSet<>();

    /**
     * Populate the check map.
     */
    public void initialize() {
        add(new FastUse());
        add(new MorePackets());
        add(new Flight());
    }

    /**
     * Add a check
     *
     * @param check the check
     */
    private void add(Check check) {
        checks.add(check);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        checks.forEach(check -> check.reload(configuration));
    }

    /**
     * Get a check
     *
     * @param checkType the type
     * @return the check
     */
    public Check getCheck(CheckType checkType) {
        return checks
                .stream()
                .filter(check -> check.type() == checkType)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Could not find check " + checkType.getName()));
    }

    /**
     * @return all checks
     */
    public Set<Check> getAllChecks() {
        return checks;
    }

    @Override
    public void close() {
        checks.forEach(Check::unload);
        checks.clear();
    }
}
