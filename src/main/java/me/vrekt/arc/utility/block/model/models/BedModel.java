package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Model for beds
 */
public final class BedModel extends BlockModel {

    public BedModel() {
        super(ModelType.BED);
    }

    @Override
    public boolean isPassable(Block block) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getCollisionBoundingBox().grow(-0.3, -0.25, -0.3);
    }
}
