package arc.check;

import arc.check.management.Checks;
import arc.check.result.CancelAction;
import arc.check.result.CheckResult;
import arc.violation.Violations;
import cn.nukkit.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Check.
 */
public abstract class Check extends CheckConfigBase {

    /**
     * The check type.
     */
    protected CheckType check;

    /**
     * A set of game-modes this player is exempt from.
     */
    protected final Set<Integer> exemptGamemodes = new HashSet<>();

    public Check(CheckType check) {
        super(check);
        this.check = check;
        Checks.register(check, this);
    }

    /**
     * Set the exempt gamemodes
     *
     * @param modes the modes
     */
    protected void setExemptGamemodes(int... modes) {
        for (final var mode : modes) exemptGamemodes.add(mode);
    }

    /**
     * Check if the player is exempt from this check because of their gamemode.
     *
     * @param player the player
     * @return {@code true} if the player is exempt
     */
    public boolean isExemptBecauseOfGamemode(Player player) {
        return exemptGamemodes.contains(player.getGamemode());
    }

    /**
     * @return the check
     */
    public CheckType check() {
        return check;
    }

    /**
     * Invoked when a violation occurs.
     *
     * @param action the action
     * @return a check result.
     */
    protected CheckResult violation(Player player, String information, CancelAction action) {
        Violations.handleViolation(player, this, information);
        if (cancel()) {
            return new CheckResult(true, action);
        } else {
            return new CheckResult(true, CancelAction.NONE);
        }
    }

}
