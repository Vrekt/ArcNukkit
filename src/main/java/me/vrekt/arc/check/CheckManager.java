package me.vrekt.arc.check;

import me.vrekt.arc.check.block.FastBreak;
import me.vrekt.arc.check.block.Nuker;
import me.vrekt.arc.check.combat.FastClick;
import me.vrekt.arc.check.combat.KillAura;
import me.vrekt.arc.check.combat.Reach;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.check.moving.Speed;
import me.vrekt.arc.check.player.FastUse;
import me.vrekt.arc.configuration.ArcConfiguration;
import me.vrekt.arc.configuration.Configurable;

import java.io.Closeable;
import java.util.*;

/**
 * A check manager
 */
public final class CheckManager implements Configurable, Closeable {

    /**
     * Map of all checks.
     */
    private final Map<CheckType, Check> checks = new HashMap<>();

    /**
     * Populate the check map.
     */
    public void initializeAllChecks() {
        add(new FastUse());
        add(new MorePackets());
        add(new Flight());
        add(new Speed());
        add(new Nuker());
        add(new Reach());
        add(new FastBreak());
        add(new KillAura());
        add(new FastClick());
    }

    /**
     * Add a check
     *
     * @param check the check
     */
    private void add(Check check) {
        checks.put(check.type(), check);
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        checks.values().forEach(check -> check.reloadConfiguration(configuration));
    }

    /**
     * Get a check
     *
     * @param check the check
     * @param <T>   the type
     * @return the check
     */
    @SuppressWarnings("unchecked")
    public <T extends Check> T getCheck(CheckType check) {
        return (T) checks.get(check);
    }

    @Override
    public void close() {
        checks.values().forEach(Check::unload);
        checks.clear();
    }
}
