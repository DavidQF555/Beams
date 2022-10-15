package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.DirectedProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Vector3d;

public abstract class DirectedProjectorBlock extends AbstractProjectorBlock {

    protected DirectedProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract Vector3d getStartOffset(DirectedProjectorTileEntity entity, BlockState state);

    public abstract Vector3d getBeamDirection(DirectedProjectorTileEntity entity, BlockState state);

}
