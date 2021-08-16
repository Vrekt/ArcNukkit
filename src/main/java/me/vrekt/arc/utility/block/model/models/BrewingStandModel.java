package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Model for brewing stand
 */
public final class BrewingStandModel extends BlockModel {

    public BrewingStandModel() {
        super(ModelType.BREWING_STAND);
    }

    @Override
    public boolean isPassable(Block block) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getCollisionBoundingBox().grow(-0.24, -0.2, -0.24);
    }

}
