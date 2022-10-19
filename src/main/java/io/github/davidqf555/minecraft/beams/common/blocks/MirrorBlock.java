package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.te.MirrorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MirrorBlock extends AbstractProjectorBlock implements IBeamCollisionEffect {

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
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
            TOP_PX = VoxelShapes.or(TOP_SLAB, OCTET_BOT_PP, OCTET_BOT_PN),
            TOP_NX = VoxelShapes.or(TOP_SLAB, OCTET_BOT_NN, OCTET_BOT_NP),
            BOT_PX = VoxelShapes.or(BOT_SLAB, OCTET_TOP_PP, OCTET_TOP_PN),
            BOT_NX = VoxelShapes.or(BOT_SLAB, OCTET_TOP_NN, OCTET_TOP_NP),
            TOP_PZ = VoxelShapes.or(TOP_SLAB, OCTET_BOT_PP, OCTET_BOT_NP),
            TOP_NZ = VoxelShapes.or(TOP_SLAB, OCTET_BOT_NN, OCTET_BOT_PN),
            BOT_PZ = VoxelShapes.or(BOT_SLAB, OCTET_TOP_PP, OCTET_TOP_NP),
            BOT_NZ = VoxelShapes.or(BOT_SLAB, OCTET_TOP_NN, OCTET_TOP_PN);

    public MirrorBlock(Properties properties) {
        super(properties);
    }

    public Face getFace(BlockState state) {
        Half half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        switch (facing) {
            case EAST:
                if (half == Half.TOP) {
                    return Face.DXN;
                }
                return Face.UXN;
            case WEST:
                if (half == Half.TOP) {
                    return Face.DXP;
                }
                return Face.UXP;
            case SOUTH:
                if (half == Half.TOP) {
                    return Face.DZN;
                }
                return Face.UZN;
            default:
                if (half == Half.TOP) {
                    return Face.DZP;
                }
                return Face.UZP;
        }
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
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
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
    public boolean isPathfindable(BlockState state, IBlockReader reader, BlockPos pos, PathType path) {
        return false;
    }

    @Override
    public boolean isActive(BlockState state) {
        return true;
    }

    @Override
    protected List<BeamEntity> shoot(World world, BlockPos pos, BlockState state) {
        List<BeamEntity> beams = new ArrayList<>();
        for (BeamEntity beam : getHit(world, pos)) {
            Vector3d start = beam.getStart();
            Vector3d hitPos = beam.position();
            Vector3d original = hitPos.subtract(start).normalize();
            Vector3d dir = getReflectedDirection(state, original);
            double width = beam.getEndWidth();
            double height = beam.getEndHeight();
            BeamEntity reflect = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, hitPos, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), beam.getModules(), width, height, width, height);
            if (reflect != null) {
                beams.add(reflect);
            }
        }
        return beams;
    }

    protected Vector3d getReflectedDirection(BlockState state, Vector3d original) {
        return getFace(state).reflect(original);
    }

    protected List<BeamEntity> getHit(World world, BlockPos pos) {
        List<BeamEntity> beams = new ArrayList<>();
        if (world instanceof ServerWorld) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof MirrorTileEntity) {
                for (UUID id : new ArrayList<>(((MirrorTileEntity) te).getHit())) {
                    Entity entity = ((ServerWorld) world).getEntity(id);
                    if (entity instanceof BeamEntity && entity.isAlive()) {
                        beams.add((BeamEntity) entity);
                    } else {
                        ((MirrorTileEntity) te).removeHit(id);
                    }
                }
            }
        }
        return beams;
    }

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof MirrorTileEntity && !((MirrorTileEntity) te).getBeams().contains(beam.getUUID()) && ((MirrorTileEntity) te).addHit(beam.getUUID())) {
            te.setChanged();
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof MirrorTileEntity && ((MirrorTileEntity) te).removeHit(beam.getUUID())) {
            te.setChanged();
        }
    }

    @Override
    public MirrorTileEntity newBlockEntity(IBlockReader reader) {
        return new MirrorTileEntity();
    }

    public enum Face {

        UXP(new Vector3d(1, 1, 0).normalize()),
        UXN(new Vector3d(-1, 1, 0).normalize()),
        DXP(new Vector3d(1, -1, 0).normalize()),
        DXN(new Vector3d(-1, -1, 0).normalize()),
        UZP(new Vector3d(0, 1, 1).normalize()),
        UZN(new Vector3d(0, 1, -1).normalize()),
        DZP(new Vector3d(0, -1, 1).normalize()),
        DZN(new Vector3d(0, -1, -1).normalize());

        private final Vector3d normal;

        Face(Vector3d normal) {
            this.normal = normal;
        }

        public Vector3d reflect(Vector3d dir) {
            return dir.subtract(normal.scale(dir.dot(normal) * 2));
        }

    }

}
