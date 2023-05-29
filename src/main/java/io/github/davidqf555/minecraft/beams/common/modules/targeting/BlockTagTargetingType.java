package io.github.davidqf555.minecraft.beams.common.modules.targeting;

import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;

public class BlockTagTargetingType implements TargetingModuleType {

    private static final double RANGE_OVERRIDE = 8;
    private final ITag<Block> tag;

    public BlockTagTargetingType(ITag<Block> tag) {
        this.tag = tag;
    }

    @Nullable
    @Override
    public Vector3d tick(TurretTileEntity te, double range) {
        World world = te.getLevel();
        BlockPos center = te.getBlockPos();
        Optional<BlockPos> closest = BlockPos.betweenClosedStream(AxisAlignedBB.ofSize(RANGE_OVERRIDE * 2, RANGE_OVERRIDE * 2, RANGE_OVERRIDE * 2).move(te.getBlockPos()))
                .filter(pos -> pos.distSqr(center) <= RANGE_OVERRIDE * RANGE_OVERRIDE)
                .filter(pos -> tag.contains(world.getBlockState(pos).getBlock()))
                .filter(pos -> canSee(world, center, pos))
                .min(Comparator.comparingDouble(center::distSqr));
        return closest.map(Vector3d::atCenterOf).orElse(null);
    }

    private boolean canSee(World world, BlockPos start, BlockPos end) {
        return world.clip(new RayTraceContext(Vector3d.atCenterOf(start), Vector3d.atCenterOf(end), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getBlockPos().equals(end);
    }

}
