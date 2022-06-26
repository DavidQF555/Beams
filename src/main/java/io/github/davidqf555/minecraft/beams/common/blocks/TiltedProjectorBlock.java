package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class TiltedProjectorBlock extends ProjectorBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public TiltedProjectorBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        Half half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        switch (facing) {
            case EAST:
                if (half == Half.TOP) {
                    return TOP_PX;
                }
                return BOT_PX;
            case WEST:
                if (half == Half.TOP) {
                    return TOP_NX;
                }
                return BOT_NX;
            case SOUTH:
                if (half == Half.TOP) {
                    return TOP_PZ;
                }
                return BOT_PZ;
            default:
                if (half == Half.TOP) {
                    return TOP_NZ;
                }
                return BOT_NZ;
        }
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
            TOP_PX = VoxelShapes.or(TOP_SLAB, OCTET_BOT_PP, OCTET_BOT_PN),
            TOP_NX = VoxelShapes.or(TOP_SLAB, OCTET_BOT_NN, OCTET_BOT_NP),
            BOT_PX = VoxelShapes.or(BOT_SLAB, OCTET_TOP_PP, OCTET_TOP_PN),
            BOT_NX = VoxelShapes.or(BOT_SLAB, OCTET_TOP_NN, OCTET_TOP_NP),
            TOP_PZ = VoxelShapes.or(TOP_SLAB, OCTET_BOT_PP, OCTET_BOT_NP),
            TOP_NZ = VoxelShapes.or(TOP_SLAB, OCTET_BOT_NN, OCTET_BOT_PN),
            BOT_PZ = VoxelShapes.or(BOT_SLAB, OCTET_TOP_PP, OCTET_TOP_NP),
            BOT_NZ = VoxelShapes.or(BOT_SLAB, OCTET_TOP_NN, OCTET_TOP_PN);

    @Override
    protected Vector3d getStartOffset(BlockState state) {
        return new Vector3d(0.5, 0.5, 0.5);
    }

    @Override
    protected Vector3d getBeamDirection(BlockState state) {
        Vector3d horz = Vector3d.atLowerCornerOf(state.getValue(FACING).getNormal()).reverse();
        Vector3d vert = new Vector3d(0, state.getValue(HALF) == Half.TOP ? -1 : 1, 0);
        return horz.add(vert).scale(0.70710678118);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction dir = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection()).setValue(HALF, dir != Direction.DOWN && (dir == Direction.UP || context.getClickLocation().y - pos.getY() <= 0.5) ? Half.BOTTOM : Half.TOP);
    }




}
