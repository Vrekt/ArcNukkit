package me.vrekt.arc.utility.block.model;

import cn.nukkit.block.Block;
import cn.nukkit.math.AxisAlignedBB;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a block model and properties about it.
 */
public abstract class BlockModel {

    private static final Map<Integer, BlockModel> MODEL_REGISTRY = new HashMap<>();

    /**
     * Basically the material of the model.
     */
    private final ModelType modelType;

    protected BlockModel(ModelType modelType) {
        this.modelType = modelType;
    }

    /**
     * @return the model type.
     */
    public ModelType getModelType() {
        return modelType;
    }

    /**
     * Register a block model.
     *
     * @param id    the ID
     * @param model the model
     */
    public static void registerBlockModel(int id, BlockModel model) {
        MODEL_REGISTRY.put(id, model);
    }

    /**
     * Get a block model
     *
     * @param block the block
     * @return the model
     */
    public static BlockModel getModel(Block block) {
        return MODEL_REGISTRY.get(block.getId());
    }

    /**
     * @return {@code true} if this model is passable in the current state.
     */
    public abstract boolean isPassable(Block block);

    /**
     * @param block the block
     * @return a modified bounding box for the block
     */
    public abstract AxisAlignedBB getBoundingBox(Block block);

    /**
     * @param block the block
     * @param bb    the bounding box
     * @return {@code true} if the provided {@code bb} collides with the block.
     */
    public boolean collides(Block block, AxisAlignedBB bb) {
        return getBoundingBox(block).intersectsWith(bb);
    }

}
