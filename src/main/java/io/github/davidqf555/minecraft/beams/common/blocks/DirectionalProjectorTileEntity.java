package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class DirectionalProjectorTileEntity extends ProjectorTileEntity {

    private UUID id;
    private Vector3d direction;

    protected DirectionalProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        direction = new Vector3d(1, 0, 0);
        id = MathHelper.createInsecureUUID();
    }

    public DirectionalProjectorTileEntity() {
        this(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get());
    }

    public Vector3d getDirection() {
        return direction;
    }

    public void setDirection(Vector3d direction) {
        this.direction = direction;
    }

    public UUID getUUID() {
        return id;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        Vector3d direction = getDirection();
        out.putDouble("DirectionX", direction.x());
        out.putDouble("DirectionY", direction.y());
        out.putDouble("DirectionZ", direction.z());
        out.putUUID("ID", getUUID());
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("DirectionX", Constants.NBT.TAG_DOUBLE) && tag.contains("DirectionY", Constants.NBT.TAG_DOUBLE) && tag.contains("DirectionZ", Constants.NBT.TAG_DOUBLE)) {
            setDirection(new Vector3d(tag.getDouble("DirectionX"), tag.getDouble("DirectionY"), tag.getDouble("DirectionZ")));
        }
        if (tag.contains("ID", Constants.NBT.TAG_INT_ARRAY)) {
            id = tag.getUUID("ID");
        }
    }
}
