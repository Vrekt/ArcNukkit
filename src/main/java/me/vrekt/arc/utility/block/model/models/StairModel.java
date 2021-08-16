package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Stair model
 */
public final class StairModel extends BlockModel {

    public StairModel() {
        super(ModelType.STAIR);
    }

    @Override
    public boolean isPassable(Block block) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getBoundingBox().grow(-0.25, -0.5, -0.25);
    }
}
