package io.github.davidqf555.minecraft.beams.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SimpleProjectorBlock extends ContainerProjectorBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final MapCodec<SimpleProjectorBlock> CODEC = simpleCodec(SimpleProjectorBlock::new);

    public SimpleProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SimpleProjectorBlock> codec() {
        return CODEC;
    }

    @Override
    public Vec3 getStartOffset(Level world, BlockPos pos, BlockState state) {
        return Vec3.atLowerCornerOf(state.getValue(FACING).getNormal()).scale(0.5).add(0.5, 0.5, 0.5);
    }

    @Override
    public Vec3 getBeamDirection(Level world, BlockPos pos, BlockState state) {
        return Vec3.atLowerCornerOf(state.getValue(FACING).getNormal());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState prev = super.getStateForPlacement(context);
        if (prev == null) {
            return null;
        }
        return prev.setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

}
