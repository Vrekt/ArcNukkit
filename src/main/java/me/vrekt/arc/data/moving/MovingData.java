package me.vrekt.arc.data.moving;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import me.vrekt.arc.utility.math.MathUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A players moving data
 */
public final class MovingData {

    /**
     * The register
     */
    private static final Map<UUID, MovingData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static MovingData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new MovingData());
    }

    /**
     * Remove data
     *
     * @param player the player
     */
    public static void remove(Player player) {
        REGISTER.remove(player.getUniqueId());
    }

    /**
     * @return temporary moving data
     */
    public static MovingData retrieveTemporary() {
        return new MovingData();
    }

    /**
     * From movement
     * To movement
     * the ground location
     * A safe location (hopefully)
     * The ladder location
     */
    private Location from, to, ground, safe, ladderLocation;

    /**
     * Arc ground and client ground
     */
    private boolean onGround, wasOnGround;

    /**
     * Ascending/Descending state
     * If we have a climbable
     * If we are climbing or not
     * If we are sneaking/sprinting
     * If we are on ice
     * If we are in liquid
     */
    private boolean ascending, descending, hasClimbable, climbing, onIce, inLiquid;

    private final AtomicBoolean sneaking = new AtomicBoolean(), sprinting = new AtomicBoolean();

    /**
     * The time we have been on ground
     * The time we have been descending
     * The time we have been ascending
     */
    private int onGroundTime, descendingTime, ascendingTime;

    /**
     * Sneaking time and sprint time
     * The time on ice
     * The time off ice
     * Invalid ground
     * The time in liquid
     * The time climbing
     */
    private int sneakTime, sprintTime, onIceTime, offIceTime, climbTime;

    /**
     * The current and last vertical distance
     */
    private double vertical, lastVertical;

    /**
     * The last moving update.
     */
    private long lastMovingUpdate;

    /**
     * Total amount of move player packets
     */
    private int movePlayerPackets;

    /**
     * If the move player packet should be cancelled.
     */
    private boolean cancelMovePlayerPacket;

    /**
     * If we had a climbable object
     */
    private boolean hadClimbable;

    /**
     * Flight descending start
     */
    private Location flightDescendingLocation;

    /**
     * IF the player was launched by a slimeblock.
     */
    private boolean hasSlimeBlockLaunch, hasSlimeblock;

    /**
     * Time in air
     */
    private int inAirTime;

    public Location from() {
        return from;
    }

    public void from(Location from) {
        this.from = from;
    }

    public Location to() {
        return to;
    }

    public void to(Location to) {
        this.to = to;
    }

    public Location ground() {
        return ground;
    }

    public void ground(Location ground) {
        this.ground = ground;
    }

    public boolean onGround() {
        return onGround;
    }

    public void onGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean wasOnGround() {
        return wasOnGround;
    }

    public void wasOnGround(boolean wasOnGround) {
        this.wasOnGround = wasOnGround;
    }

    public boolean ascending() {
        return ascending;
    }

    public void ascending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean descending() {
        return descending;
    }

    public void descending(boolean descending) {
        this.descending = descending;
    }

    public boolean hasClimbable() {
        return hasClimbable;
    }

    public void hasClimbable(boolean hasClimbable) {
        this.hasClimbable = hasClimbable;
    }

    public boolean climbing() {
        return climbing;
    }

    public void climbing(boolean climbing) {
        this.climbing = climbing;
    }

    public boolean sneaking() {
        return sneaking.get();
    }

    public void sneaking(boolean sneaking) {
        this.sneaking.set(sneaking);
    }

    public boolean sprinting() {
        return sprinting.get();
    }

    public void sprinting(boolean sprinting) {
        this.sprinting.set(sprinting);
    }

    public int onGroundTime() {
        return onGroundTime;
    }

    public void onGroundTime(int onGroundTime) {
        this.onGroundTime = MathUtil.clampInt(onGroundTime, 0, 1000);
    }

    public void incrementOnGroundTime() {
        onGroundTime(onGroundTime + 1);
    }

    public int descendingTime() {
        return descendingTime;
    }

    public void descendingTime(int descendingTime) {
        this.descendingTime = MathUtil.clampInt(descendingTime, 0, 1000);
    }

    public void incrementDescendingTime() {
        descendingTime(descendingTime + 1);
    }

    public int ascendingTime() {
        return ascendingTime;
    }

    public void ascendingTime(int ascendingTime) {
        this.ascendingTime = MathUtil.clampInt(ascendingTime, 0, 1000);
    }

    public void incrementAscendingTime() {
        ascendingTime(ascendingTime + 1);
    }

    public int sneakTime() {
        return sneakTime;
    }

    public void sneakTime(int sneakTime) {
        this.sneakTime = MathUtil.clampInt(sneakTime, 0, 1000);
    }

    public void incrementSneakTime() {
        sneakTime(sneakTime + 1);
    }

    public int sprintTime() {
        return sprintTime;
    }

    public void sprintTime(int sprintTime) {
        this.sprintTime = MathUtil.clampInt(sprintTime, 0, 1000);
    }

    public void incrementSprintTime() {
        sprintTime(sprintTime + 1);
    }

    public int onIceTime() {
        return onIceTime;
    }

    public void onIceTime(int onIceTime) {
        this.onIceTime = MathUtil.clampInt(onIceTime, 0, 1000);
    }

    public void incrementOnIceTime() {
        onIceTime(onIceTime + 1);
    }

    public int offIceTime() {
        return offIceTime;
    }

    public void offIceTime(int offIceTime) {
        this.offIceTime = MathUtil.clampInt(offIceTime, 0, 1000);
    }

    public void incrementOffIceTime() {
        offIceTime(offIceTime + 1);
    }

    public boolean onIce() {
        return onIce;
    }

    public void onIce(boolean onIce) {
        this.onIce = onIce;
    }

    public double vertical() {
        return vertical;
    }

    public void vertical(double vertical) {
        this.vertical = vertical;
    }

    public double lastVertical() {
        return lastVertical;
    }

    public void lastVertical(double lastVertical) {
        this.lastVertical = lastVertical;
    }

    public long lastMovingUpdate() {
        return lastMovingUpdate;
    }

    public void lastMovingUpdate(long lastMovingUpdate) {
        this.lastMovingUpdate = lastMovingUpdate;
    }

    public boolean inLiquid() {
        return inLiquid;
    }

    public void inLiquid(boolean inLiquid) {
        this.inLiquid = inLiquid;
    }

    public int climbTime() {
        return climbTime;
    }

    public void climbTime(int climbTime) {
        this.climbTime = MathUtil.clampInt(climbTime, 0, 100);
    }

    public int movePlayerPackets() {
        return movePlayerPackets;
    }

    public void movePlayerPackets(int movePlayerPackets) {
        this.movePlayerPackets = movePlayerPackets;
    }

    /**
     * Increment the total {@code movePlayerPackets} and return if we should cancel.
     *
     * @return {@code true} if to cancel.
     */
    public boolean incrementMovePlayerPacketsAndCheckCancel() {
        this.movePlayerPackets = movePlayerPackets + 1;
        return cancelMovePlayerPacket;
    }

    public void cancelMovePlayerPacket(boolean cancelMovePlayerPacket) {
        this.cancelMovePlayerPacket = cancelMovePlayerPacket;
    }

    public boolean hadClimbable() {
        return hadClimbable;
    }

    public void hadClimbable(boolean hadClimbable) {
        this.hadClimbable = hadClimbable;
    }

    public Location getSafeLocation() {
        return safe;
    }

    public void setSafeLocation(Location safeLocation) {
        this.safe = safeLocation;
    }

    public boolean hasSlimeBlockLaunch() {
        return hasSlimeBlockLaunch;
    }

    public void setHasSlimeBlockLaunch(boolean hasSlimeBlockLaunch) {
        this.hasSlimeBlockLaunch = hasSlimeBlockLaunch;
    }

    public Location getFlightDescendingLocation() {
        return flightDescendingLocation;
    }

    public void setFlightDescendingLocation(Location flightDescendingLocation) {
        this.flightDescendingLocation = flightDescendingLocation;
    }

    public boolean hasSlimeblock() {
        return hasSlimeblock;
    }

    public void setHasSlimeblock(boolean hasSlimeblock) {
        this.hasSlimeblock = hasSlimeblock;
    }

    public int getInAirTime() {
        return inAirTime;
    }

    public void setInAirTime(int inAirTime) {
        this.inAirTime = MathUtil.clampInt(inAirTime, 0, 100);
    }

}
