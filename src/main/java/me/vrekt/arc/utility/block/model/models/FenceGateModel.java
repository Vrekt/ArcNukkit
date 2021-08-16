package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Represents a model for a fence gate.
 */
public final class FenceGateModel extends BlockModel {

    public FenceGateModel() {
        super(ModelType.FENCE_GATE);
    }

    @Override
    public boolean isPassable(Block block) {
        return ((BlockFenceGate) block).isOpen();
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getBoundingBox();
    }
}
