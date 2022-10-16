package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class AbstractProjectorTileEntity extends TileEntity {

    protected UUID beam;

    public AbstractProjectorTileEntity(TileEntityType<?> type) {
        super(type);
    }

    protected void updateBeam() {
        removeBeam();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (block instanceof AbstractProjectorBlock && ((AbstractProjectorBlock) block).isActive(state)) {
            shoot();
        }
    }

    @Override
    public void setChanged() {
        updateBeam();
        super.setChanged();
    }

    protected abstract void shoot();

    public void removeBeam() {
        World world = getLevel();
        if (beam != null && world instanceof ServerWorld) {
            Entity entity = ((ServerWorld) world).getEntity(beam);
            if (entity != null) {
                entity.remove();
            }
            beam = null;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        if (beam != null) {
            out.putUUID("Beam", beam);
        }
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("Beam", Constants.NBT.TAG_INT_ARRAY)) {
            beam = tag.getUUID("Beam");
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        deserializeNBT(pkt.getTag());
    }

}
