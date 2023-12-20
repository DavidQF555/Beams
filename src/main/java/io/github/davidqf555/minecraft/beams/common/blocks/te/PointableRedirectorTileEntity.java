package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class PointableRedirectorTileEntity extends RedirectorTileEntity {

    private UUID id;
    private Vector3d normal;

    protected PointableRedirectorTileEntity(TileEntityType<?> type) {
        super(type);
        normal = new Vector3d(1, 0, 0);
        id = MathHelper.createInsecureUUID();
    }

    public PointableRedirectorTileEntity() {
        this(TileEntityRegistry.OMNIDIRECTIONAL_MIRROR.get());
    }

    public Vector3d getNormal() {
        return normal;
    }

    public void setNormal(Vector3d normal) {
        this.normal = normal;
    }

    public UUID getUUID() {
        return id;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        Vector3d direction = getNormal();
        out.putDouble("NormalX", direction.x());
        out.putDouble("NormalY", direction.y());
        out.putDouble("NormalZ", direction.z());
        out.putUUID("ID", getUUID());
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("NormalX", Constants.NBT.TAG_DOUBLE) && tag.contains("NormalY", Constants.NBT.TAG_DOUBLE) && tag.contains("NormalZ", Constants.NBT.TAG_DOUBLE)) {
            setNormal(new Vector3d(tag.getDouble("NormalX"), tag.getDouble("NormalY"), tag.getDouble("NormalZ")));
        }
        if (tag.contains("ID", Constants.NBT.TAG_INT_ARRAY)) {
            id = tag.getUUID("ID");
        }
    }

}
