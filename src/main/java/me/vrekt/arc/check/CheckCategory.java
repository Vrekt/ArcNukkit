package me.vrekt.arc.check;

import me.vrekt.arc.permissions.Permissions;

/**
 * Represents a check category.
 */
public enum CheckCategory {

    /**
     * Checks movement
     */
    MOVING,
    /**
     * Checks other player related things
     */
    PLAYER,
    /**
     * Checks packets
     */
    NETWORK,
    /**
     * Checks combat
     */
    COMBAT,

    /**
     * Checks block related things
     */
    BLOCK;

    /**
     * Bypass permission
     */
    private final String bypassPermission;

    CheckCategory() {
        this.bypassPermission = Permissions.ARC_BYPASS + "." + name();
    }

    /**
     * @return the bypass permission
     */
    public String getBypassPermission() {
        return bypassPermission;
    }

}
