package me.vrekt.arc.check.moving;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import me.vrekt.arc.check.Check;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.result.CheckResult;
import me.vrekt.arc.compatibility.block.BlockAccess;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.models.*;
import me.vrekt.arc.utility.block.ray.AxisRayTracing;

/**
 * Checks if the player is moving through blocks.
 */
public final class Phase extends Check {

    public Phase() {
        super(CheckType.PHASE);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .banLevel(20)
                .kick(false)
                .build();

        if (enabled()) load();
    }

    /**
     * Check a player for phase
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    public boolean check(Player player, MovingData data) {
        if (exempt(player)) return false;

        final Location from = data.from();
        final Location to = data.to();
        Location safe = data.getSafePhaseLocation();

        if (safe == null) {
            safe = from;
            data.setSafePhaseLocation(from);
        }

        final long now = System.currentTimeMillis();
        final long timeSinceLastCollision = now - data.getLastCollisionEvent();

        final AxisAlignedBB bb = player.boundingBox.grow(-0.25, 0.0, -0.25);
        final CheckResult result = new CheckResult();

        final AxisRayTracing rayTracing = new AxisRayTracing();

        rayTracing.set(safe, to, bb);
        rayTracing.loop();

        if (rayTracing.collides()) {
            data.setLastCollisionEvent(System.currentTimeMillis());
            player.sendMessage(TextFormat.RED + "FLAG");
            player.teleport(safe);
        } else {
            if (System.currentTimeMillis() - data.getLastCollisionEvent() >= 2000 && data.onGround()) {
                data.setSafePhaseLocation(from);
            }
        }

        return false;
    }

    /**
     * Check the block state, and ensure the player cannot move through it.
     *
     * @param block  the block
     * @param bb     the bounding box
     * @param safe   the safe location
     * @param to     the to location
     * @param result the result
     * @return {@code true} if the block cannot be moved through
     */
    private boolean checkBlockState(Block block, AxisAlignedBB bb, Location safe, Location to, CheckResult result) {
        if (isSolid(block)) {
            // we have a solid block, look into it further.
            final BlockModel model = BlockModel.getModel(block);
            final boolean hasCollisionNormal = block.collidesWithBB(bb);

            if (model == null) {
                final Block blockBelow = BlockAccess.getBlockAt(to, to.level, 0.3, -0.2, 0.3);
                System.out.println("Below is " + blockBelow);

                final BlockModel maybe = BlockModel.getModel(blockBelow);
                if (maybe != null) {
                    return checkBlockModel(block, maybe, bb, safe, to, result);
                }

                return hasCollisionNormal;
            } else {
                if (hasCollisionNormal) {
                    return checkBlockModel(block, model, bb, safe, to, result);
                }
            }
        }

        return false;
    }

    /**
     * Check block model
     *
     * @param block  the block
     * @param model  the model
     * @param bb     the bounding box
     * @param safe   the safe location
     * @param to     the to location
     * @param result the result
     * @return {@code true} if the block isn't passable.
     */
    private boolean checkBlockModel(Block block, BlockModel model, AxisAlignedBB bb, Location safe, Location to, CheckResult result) {
        final boolean hasCollision = model.collides(block, bb);

        if (hasCollision) {
            result.withParameter("modelCheck", "yes");
            return !isPassable(model, block);
        } else {
            // calculate area between safe and where we moved to.
            int minX = Math.min(safe.getFloorX(), to.getFloorX());
            int maxX = Math.max(safe.getFloorX(), to.getFloorX());
            int minY = Math.min(safe.getFloorY(), to.getFloorY());
            int maxY = Math.max(safe.getFloorY(), to.getFloorY());
            int minZ = Math.min(safe.getFloorZ(), to.getFloorZ());
            int maxZ = Math.max(safe.getFloorZ(), to.getFloorZ());

            result.withParameter("areaCheck", "yes");
            // create a bounding box from that.
            final AxisAlignedBB area = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
            // check if the block was inside.
            final boolean isIgnore = ignoreInAreaCheck(model);
            final boolean areaCollision = area.isVectorInside(new Vector3(block.x, block.y, block.z));
            return false;
        }
    }

    /**
     * Check if a block is passable
     *
     * @param model the model
     * @param block the block
     * @return {@code true} if so
     */
    private boolean isPassable(BlockModel model, Block block) {
        boolean valid;

        switch (model.getModelType()) {
            case FENCE_GATE:
            case TRAPDOOR:
                valid = model.isPassable(block);
                break;
            default:
                valid = false;
        }
        return valid;
    }

    /**
     * Check if a block should be ignored during area checks.
     *
     * @param model the mode
     * @return {@code true} if so
     */
    private boolean ignoreInAreaCheck(BlockModel model) {
        switch (model.getModelType()) {
            case SLAB:
            case STAIR:
            case CARPET:
            case BED:
            case BREWING_STAND:
            case DAYLIGHT_SENSOR:
            case TRAPDOOR:
                return true;
        }
        return false;
    }

    /**
     * Check if there are any blocks between a location
     *
     * @param safe the safe
     * @param to   the to
     * @return {@code true} if so
     */
    public boolean hasAnyBlocksBetweenSafe(Location safe, Location to) {
        int x1 = Math.min(safe.getFloorX(), to.getFloorX());
        int x2 = Math.max(safe.getFloorX(), to.getFloorX());
        int y1 = Math.min(safe.getFloorY(), to.getFloorY());
        int y2 = Math.max(safe.getFloorY(), to.getFloorY());
        int z1 = Math.min(safe.getFloorZ(), to.getFloorZ());
        int z2 = Math.max(safe.getFloorZ(), to.getFloorZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    // Hard check, anything solid regardless of state? Nope.
                    final Block block = safe.getLevel().getBlock(x, y, z);
                    if (isSolid(block)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the block is considered solid.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public boolean isSolid(Block block) {
        if (!block.isSolid()) {
            switch (block.getId()) {
                case Block.GLASS_PANE:
                case Block.STAINED_GLASS_PANE:
                case Block.DOOR_BLOCK:
                case Block.IRON_BAR:
                    return true;
                default:
                    return false;
            }
        }
        return block.isSolid();
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        registerBlockModels();
    }

    /**
     * "
     * Register block models
     */
    private void registerBlockModels() {
        final FenceGateModel fenceGateModel = new FenceGateModel();
        BlockModel.registerBlockModel(Block.FENCE_GATE, fenceGateModel);
        BlockModel.registerBlockModel(Block.FENCE_GATE_ACACIA, fenceGateModel);
        BlockModel.registerBlockModel(Block.FENCE_GATE_BIRCH, fenceGateModel);
        BlockModel.registerBlockModel(Block.FENCE_GATE_DARK_OAK, fenceGateModel);
        BlockModel.registerBlockModel(Block.FENCE_GATE_JUNGLE, fenceGateModel);
        BlockModel.registerBlockModel(Block.FENCE_GATE_SPRUCE, fenceGateModel);
        BlockModel.registerBlockModel(Block.FENCE, new FenceModel());
        final GlassPaneModel glassPaneModel = new GlassPaneModel();
        BlockModel.registerBlockModel(Block.GLASS_PANE, glassPaneModel);
        BlockModel.registerBlockModel(Block.STAINED_GLASS_PANE, glassPaneModel);
        final StairModel stairModel = new StairModel();
        BlockModel.registerBlockModel(Block.COBBLESTONE_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.ACACIA_WOOD_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.SANDSTONE_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.BIRCH_WOOD_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.SPRUCE_WOOD_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.STONE_BRICK_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.BRICK_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.DARK_OAK_WOOD_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.JUNGLE_WOOD_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.NETHER_BRICKS_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.PURPUR_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.RED_SANDSTONE_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.QUARTZ_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.JUNGLE_WOODEN_STAIRS, stairModel);
        BlockModel.registerBlockModel(Block.BED_BLOCK, new BedModel());
        final SlabModel slabModel = new SlabModel();
        BlockModel.registerBlockModel(Block.SLAB, slabModel);
        BlockModel.registerBlockModel(Block.STONE_SLAB, slabModel);
        BlockModel.registerBlockModel(Block.WOOD_SLAB, slabModel);
        BlockModel.registerBlockModel(Block.RED_SANDSTONE_SLAB, slabModel);
        BlockModel.registerBlockModel(Block.BREWING_STAND_BLOCK, new BrewingStandModel());
        final DaylightSensorModel daylightSensorModel = new DaylightSensorModel();
        BlockModel.registerBlockModel(Block.DAYLIGHT_DETECTOR_INVERTED, daylightSensorModel);
        BlockModel.registerBlockModel(Block.DAYLIGHT_DETECTOR, daylightSensorModel);
        final TrapdoorModel trapdoorModel = new TrapdoorModel();
        BlockModel.registerBlockModel(Block.TRAPDOOR, trapdoorModel);
        BlockModel.registerBlockModel(Block.IRON_TRAPDOOR, trapdoorModel);
        BlockModel.registerBlockModel(Block.IRON_BARS, new IronBarsModel());
        BlockModel.registerBlockModel(Block.CARPET, new CarpetModel());
    }

}
