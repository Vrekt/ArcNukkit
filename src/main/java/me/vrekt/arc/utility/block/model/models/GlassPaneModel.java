package me.vrekt.arc.utility.block.model.models;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;
import me.vrekt.arc.utility.block.model.BlockModel;
import me.vrekt.arc.utility.block.model.ModelType;

/**
 * Glass pane model.
 */
public final class GlassPaneModel extends BlockModel {

    public GlassPaneModel() {
        super(ModelType.GLASS_PANE);
    }

    @Override
    public boolean isPassable(Block block) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(Block block) {
        return block.getCollisionBoundingBox().grow(-0.24, 0.0, -0.24);
    }

}
