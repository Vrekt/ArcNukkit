package me.vrekt.arc.permissions;

import cn.nukkit.Player;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckCategory;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.configuration.ArcConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Arc permissions
 */
public final class Permissions {

    /**
     * The violations permission
     */
    public static final String ARC_VIOLATIONS = "arc.violations";

    /**
     * The bypass permission
     */
    public static final String ARC_BYPASS = "arc.bypass";

    /**
     * The administrator permission
     */
    public static final String ARC_ADMINISTRATOR = "arc.administrator";

    /**
     * The base command permission.
     */
    public static final String ARC_COMMANDS_BASE = "arc.commands.base";

    /**
     * The permission to toggle violations
     */
    public static final String ARC_COMMANDS_TOGGLE_VIOLATIONS = "arc.commands.toggleviolations";

    /**
     * The permission to cancel player bans
     */
    public static final String ARC_COMMANDS_CANCEL_BAN = "arc.commands.cancelban";

    /**
     * The permission to reload the config
     */
    public static final String ARC_COMMANDS_RELOAD_CONFIG = "arc.commands.reloadconfig";

    /**
     * The permission for viewing debug information
     */
    public static final String ARC_COMMANDS_DEBUG = "arc.commands.debug";

    /**
     * The permission for viewing timings information.
     */
    public static final String ARC_COMMANDS_TIMINGS = "arc.commands.timings";

    /**
     * The permission for viewing violation history
     */
    public static final String ARC_COMMANDS_VIOLATION_HISTORY = "arc.commands.violationhistory";

    /**
     * The permission to exempt from certain checks
     */
    public static final String ARC_COMMANDS_EXEMPT = "arc.commands.exempt";

    /**
     * The permission to view player summaries
     */
    public static final String ARC_COMMANDS_SUMMARY = "arc.commands.summary";

    /**
     * The permission to execute all arc commands
     */
    public static final String ARC_COMMANDS_ALL = "arc.commands.all";

    /**
     * Config instance.
     */
    private static final ArcConfiguration CONFIGURATION = Arc.getInstance().getArcConfiguration();

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public static boolean canViewViolations(Player player) {
        return player.hasPermission(ARC_VIOLATIONS) || (CONFIGURATION.canOpViewViolations() && player.isOp());
    }

    /**
     * Check if the player can bypass checks
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public static boolean canBypassAllChecks(Player player) {
        if (player == null || !player.isOnline()) return true;
        return player.hasPermission(ARC_BYPASS) || (CONFIGURATION.canOpBypass() && player.isOp());
    }

    public static List<CheckType> getChecksCanBypass(Player player) {
        if (canBypassAllChecks(player)) return Arrays.asList(CheckType.values());

        final List<CheckType> checks = new ArrayList<>();
        for (CheckType type : CheckType.values()) {
            if (canBypassCheck(player, type)) checks.add(type);
        }
        return checks;
    }

    /**
     * Check if a player can bypass a category all together
     *
     * @param player   the player
     * @param category the category
     * @return {@code true} if so
     */
    public static boolean canBypassCategory(Player player, CheckCategory category) {
        if (player == null || !player.isOnline() || canBypassAllChecks(player)) return true;
        return player.hasPermission(category.getBypassPermission()) || (CONFIGURATION.canOpBypass() && player.isOp());
    }

    /**
     * Check if the player can bypass the provided checks.
     *
     * @param player the player
     * @param check  check
     * @return {@code true} if so.
     */
    public static boolean canBypassCheck(Player player, CheckType check) {
        if (player == null || !player.isOnline() || canBypassAllChecks(player)) return true;
        return player.hasPermission(check.getCategory().getBypassPermission())
                || player.hasPermission(check.getBypassPermission())
                || (CONFIGURATION.canOpBypass() && player.isOp());
    }
}
