package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class OmnidirectionalProjectorTileEntity extends ContainerProjectorTileEntity {

    private Vec3 direction;

    protected OmnidirectionalProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        direction = new Vec3(1, 0, 0);
    }

    public OmnidirectionalProjectorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), pos, state);
    }

    public Vec3 getDirection() {
        return direction;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction.normalize();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("DirectionX", direction.x());
        tag.putDouble("DirectionY", direction.y());
        tag.putDouble("DirectionZ", direction.z());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("DirectionX", Tag.TAG_DOUBLE) && tag.contains("DirectionY", Tag.TAG_DOUBLE) && tag.contains("DirectionZ", Tag.TAG_DOUBLE)) {
            setDirection(new Vec3(tag.getDouble("DirectionX"), tag.getDouble("DirectionY"), tag.getDouble("DirectionZ")));
        }
    }
}
