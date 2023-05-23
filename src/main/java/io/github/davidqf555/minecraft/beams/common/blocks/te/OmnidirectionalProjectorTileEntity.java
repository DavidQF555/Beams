package io.github.davidqf555.minecraft.beams.common.blocks.te;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

public class OmnidirectionalProjectorTileEntity extends ContainerProjectorTileEntity {

    private Vector3d direction;

    protected OmnidirectionalProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        direction = new Vector3d(1, 0, 0);
    }

    public Vector3d getDirection() {
        return direction;
    }

    public void setDirection(Vector3d direction) {
        this.direction = direction;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        Vector3d direction = getDirection();
        out.putDouble("DirectionX", direction.x());
        out.putDouble("DirectionY", direction.y());
        out.putDouble("DirectionZ", direction.z());
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("DirectionX", Constants.NBT.TAG_DOUBLE) && tag.contains("DirectionY", Constants.NBT.TAG_DOUBLE) && tag.contains("DirectionZ", Constants.NBT.TAG_DOUBLE)) {
            setDirection(new Vector3d(tag.getDouble("DirectionX"), tag.getDouble("DirectionY"), tag.getDouble("DirectionZ")));
        }
    }
}
