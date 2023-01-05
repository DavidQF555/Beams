package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SimpleMirrorBlock extends AbstractMirrorBlock {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape
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

    public SimpleMirrorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityRegistry.MIRROR.get(), AbstractProjectorTileEntity::tick);
    }

    public Face getFace(BlockState state) {
        Half half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        switch (facing) {
            case EAST -> {
                if (half == Half.TOP) {
                    return Face.DXN;
                }
                return Face.UXN;
            }
            case WEST -> {
                if (half == Half.TOP) {
                    return Face.DXP;
                }
                return Face.UXP;
            }
            case SOUTH -> {
                if (half == Half.TOP) {
                    return Face.DZN;
                }
                return Face.UZN;
            }
            default -> {
                if (half == Half.TOP) {
                    return Face.DZP;
                }
                return Face.UZP;
            }
        }
    }

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState prev = super.getStateForPlacement(context);
        if (prev == null) {
            return null;
        }
        Direction dir = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        return prev.setValue(FACING, context.getHorizontalDirection()).setValue(HALF, dir != Direction.DOWN && (dir == Direction.UP || context.getClickLocation().y - pos.getY() <= 0.5) ? Half.BOTTOM : Half.TOP);
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType path) {
        return false;
    }

    @Override
    protected Vec3 getFaceNormal(Level world, BlockPos pos, BlockState state) {
        return getFace(state).getNormal();
    }

    public enum Face {

        UXP(new Vec3(1, 1, 0).normalize()),
        UXN(new Vec3(-1, 1, 0).normalize()),
        DXP(new Vec3(1, -1, 0).normalize()),
        DXN(new Vec3(-1, -1, 0).normalize()),
        UZP(new Vec3(0, 1, 1).normalize()),
        UZN(new Vec3(0, 1, -1).normalize()),
        DZP(new Vec3(0, -1, 1).normalize()),
        DZN(new Vec3(0, -1, -1).normalize());

        private final Vec3 normal;

        Face(Vec3 normal) {
            this.normal = normal;
        }

        public Vec3 getNormal() {
            return normal;
        }

    }

}
