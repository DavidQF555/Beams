package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.BeamTurretBlock;
import io.github.davidqf555.minecraft.beams.common.items.TargetingModuleItem;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class TurretTileEntity extends OmnidirectionalProjectorTileEntity {

    private static final double START_DIST = 0.2;
    private static final double STOP_DIST = 0.5;
    private static final float ANGULAR_SPEED = (float) (Math.PI / 10000);
    protected final NonNullList<ItemStack> targeting = NonNullList.withSize(3, ItemStack.EMPTY);

    protected TurretTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public TurretTileEntity() {
        this(TileEntityRegistry.TURRET.get());
    }

    @Override
    public void tick() {
        if (hasLevel()) {
            BlockState state = getBlockState();
            if (state.getValue(BeamTurretBlock.TRIGGERED)) {
                boolean on = state.getValue(BeamTurretBlock.IN_RANGE);
                boolean hasTarget = false;
                for (ItemStack stack : targeting) {
                    Item item = stack.getItem();
                    if (!stack.isEmpty() && item instanceof TargetingModuleItem) {
                        Vector3d target = ((TargetingModuleItem) item).getType().tick(this, ServerConfigs.INSTANCE.projectorMaxRange.get());
                        if (target != null) {
                            hasTarget = true;
                            BlockPos pos = getBlockPos();
                            if (on) {
                                if (shouldStop(target)) {
                                    getLevel().setBlockAndUpdate(pos, state.setValue(BeamTurretBlock.IN_RANGE, false));
                                    markChanged();
                                }
                            } else if (shouldStart(target)) {
                                getLevel().setBlockAndUpdate(pos, state.setValue(BeamTurretBlock.IN_RANGE, true));
                                markChanged();
                            } else {
                                rotateTowards(target.subtract(Vector3d.atCenterOf(pos)).normalize());
                            }
                            break;
                        }
                    }
                }
                if (!hasTarget && on) {
                    getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(BeamTurretBlock.IN_RANGE, false));
                    markChanged();
                }
            }
        }
        super.tick();
    }

    private void rotateTowards(Vector3d direction) {
        Vector3d current = getDirection();
        Vector3d comp = direction.cross(current.cross(direction));
        setDirection(direction.scale(MathHelper.cos(ANGULAR_SPEED)).add(comp.scale(MathHelper.sin(ANGULAR_SPEED))));
    }

    protected boolean shouldStart(Vector3d target) {
        return getBeamDistanceSqr(target) <= START_DIST * START_DIST;
    }

    protected boolean shouldStop(Vector3d target) {
        return getBeamDistanceSqr(target) >= STOP_DIST * STOP_DIST;
    }

    private double getBeamDistanceSqr(Vector3d target) {
        Vector3d current = getDirection();
        Vector3d expected = target.subtract(Vector3d.atCenterOf(getBlockPos()));
        return current.scale(expected.lengthSqr() / current.dot(expected)).subtract(expected).lengthSqr();
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new TurretContainer(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return super.getContainerSize() + targeting.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && targeting.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        int dif = super.getContainerSize();
        return index >= dif && index < getContainerSize() ? targeting.get(index - dif) : super.getItem(index);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        int dif = super.getContainerSize();
        if (index < dif) {
            return super.removeItem(index, amount);
        }
        ItemStack stack = ItemStackHelper.removeItem(targeting, index - dif, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        int dif = super.getContainerSize();
        if (index < dif) {
            return super.removeItemNoUpdate(index);
        }
        return ItemStackHelper.takeItem(targeting, index - dif);
    }

    @Override
    public void setItemNoUpdate(int index, ItemStack stack) {
        int dif = super.getContainerSize();
        if (index < dif) {
            super.setItemNoUpdate(index, stack);
        } else {
            targeting.set(index - dif, stack);
            if (stack.getCount() > getMaxStackSize()) {
                stack.setCount(getMaxStackSize());
            }
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        int dif = super.getContainerSize();
        if (index < dif) {
            super.setItem(index, stack);
        } else {
            setItemNoUpdate(index, stack);
            setChanged();
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        targeting.clear();
    }

}
