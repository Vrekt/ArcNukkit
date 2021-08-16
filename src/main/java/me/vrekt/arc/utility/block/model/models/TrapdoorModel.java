package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTrapdoor;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Model for trapdoors
 */
public final class TrapdoorModel extends BlockModel {

    public TrapdoorModel() {
        super(ModelType.TRAPDOOR);
    }

    @Override
    public boolean isPassable(Block block) {
        return ((BlockTrapdoor) block).isOpen();
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getCollisionBoundingBox().grow(0.0, -0.25, 0.0);
    }
}
