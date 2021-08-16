package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * A model for all fences
 */
public final class FenceModel extends BlockModel {

    public FenceModel() {
        super(ModelType.FENCE);
    }

    @Override
    public boolean isPassable(Block block) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getBoundingBox().grow(-0.2, 0.0, -0.2);
    }
}
