package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Model for carpet
 */
public final class CarpetModel extends BlockModel {

    public CarpetModel() {
        super(ModelType.CARPET);
    }

    @Override
    public boolean isPassable(Block block) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getCollisionBoundingBox().grow(0.0, -0.75, 0.0);
    }
}
