package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class OmnidirectionalProjectorBlock extends AbstractProjectorBlock {

    public OmnidirectionalProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Vec3 getStartOffset(ProjectorTileEntity entity, BlockState state) {
        Vec3 pos = new Vec3(0.5, 0.5, 0.5);
        if (entity instanceof DirectionalProjectorTileEntity) {
            pos = pos.add(((DirectionalProjectorTileEntity) entity).getDirection().scale(0.25));
        }
        return pos;
    }

    @Override
    protected Vec3 getBeamDirection(ProjectorTileEntity entity, BlockState state) {
        if (entity instanceof DirectionalProjectorTileEntity) {
            return ((DirectionalProjectorTileEntity) entity).getDirection();
        }
        return Vec3.ZERO;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DirectionalProjectorTileEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext selection) {
        return Shapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter reader, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

}
