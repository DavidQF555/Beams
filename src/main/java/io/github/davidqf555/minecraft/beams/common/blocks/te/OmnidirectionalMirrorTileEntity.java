package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class OmnidirectionalMirrorTileEntity extends MirrorTileEntity {

    private UUID id;
    private Vec3 normal;

    protected OmnidirectionalMirrorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        normal = new Vec3(1, 0, 0);
        id = Mth.createInsecureUUID();
    }

    public OmnidirectionalMirrorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.OMNIDIRECTIONAL_MIRROR.get(), pos, state);
    }

    public Vec3 getNormal() {
        return normal;
    }

    public void setNormal(Vec3 normal) {
        this.normal = normal;
    }

    public UUID getUUID() {
        return id;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        Vec3 direction = getNormal();
        tag.putDouble("NormalX", direction.x());
        tag.putDouble("NormalY", direction.y());
        tag.putDouble("NormalZ", direction.z());
        tag.putUUID("ID", getUUID());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("NormalX", Tag.TAG_DOUBLE) && tag.contains("NormalY", Tag.TAG_DOUBLE) && tag.contains("NormalZ", Tag.TAG_DOUBLE)) {
            setNormal(new Vec3(tag.getDouble("NormalX"), tag.getDouble("NormalY"), tag.getDouble("NormalZ")));
        }
        if (tag.contains("ID", Tag.TAG_INT_ARRAY)) {
            id = tag.getUUID("ID");
        }
    }

}
