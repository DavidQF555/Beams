package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TiltedProjectorBlock extends ProjectorBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public TiltedProjectorBlock(Properties properties) {
        super(properties);
    }    private static final VoxelShape
            TOP_SLAB = Block.box(0, 8, 0, 16, 16, 16),
            BOT_SLAB = Block.box(0, 0, 0, 16, 8, 16),
            OCTET_TOP_PP = Block.box(8, 8, 8, 16, 16, 16),
            OCTET_TOP_PN = Block.box(8, 8, 0, 16, 16, 8),
            OCTET_TOP_NP = Block.box(0, 8, 8, 8, 16, 16),
            OCTET_TOP_NN = Block.box(0, 8, 0, 8, 16, 8),
            OCTET_BOT_PP = Block.box(8, 0, 8, 16, 8, 16),
            OCTET_BOT_PN = Block.box(8, 0, 0, 16, 8, 8),
            OCTET_BOT_NP = Block.box(0, 0, 8, 8, 8, 16),
            OCTET_BOT_NN = Block.box(0, 0, 0, 8, 8, 8),
            TOP_PX = Shapes.or(TOP_SLAB, OCTET_BOT_PP, OCTET_BOT_PN),
            TOP_NX = Shapes.or(TOP_SLAB, OCTET_BOT_NN, OCTET_BOT_NP),
            BOT_PX = Shapes.or(BOT_SLAB, OCTET_TOP_PP, OCTET_TOP_PN),
            BOT_NX = Shapes.or(BOT_SLAB, OCTET_TOP_NN, OCTET_TOP_NP),
            TOP_PZ = Shapes.or(TOP_SLAB, OCTET_BOT_PP, OCTET_BOT_NP),
            TOP_NZ = Shapes.or(TOP_SLAB, OCTET_BOT_NN, OCTET_BOT_PN),
            BOT_PZ = Shapes.or(BOT_SLAB, OCTET_TOP_PP, OCTET_TOP_NP),
            BOT_NZ = Shapes.or(BOT_SLAB, OCTET_TOP_NN, OCTET_TOP_PN);

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        Half half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        switch (facing) {
            case EAST -> {
                if (half == Half.TOP) {
                    return TOP_PX;
                }
                return BOT_PX;
            }
            case WEST -> {
                if (half == Half.TOP) {
                    return TOP_NX;
                }
                return BOT_NX;
            }
            case SOUTH -> {
                if (half == Half.TOP) {
                    return TOP_PZ;
                }
                return BOT_PZ;
            }
            default -> {
                if (half == Half.TOP) {
                    return TOP_NZ;
                }
                return BOT_NZ;
            }
        }
    }

    @Override
    protected Vec3 getStartOffset(BlockState state) {
        return new Vec3(0.5, 0.5, 0.5);
    }

    @Override
    protected Vec3 getBeamDirection(BlockState state) {
        Vec3 horz = Vec3.atLowerCornerOf(state.getValue(FACING).getNormal()).reverse();
        Vec3 vert = new Vec3(0, state.getValue(HALF) == Half.TOP ? -1 : 1, 0);
        return horz.add(vert).scale(0.70710678118);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection()).setValue(HALF, dir != Direction.DOWN && (dir == Direction.UP || context.getClickLocation().y - pos.getY() <= 0.5) ? Half.BOTTOM : Half.TOP);
    }



}
