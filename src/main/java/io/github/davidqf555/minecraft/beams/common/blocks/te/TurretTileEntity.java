package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.BeamTurretBlock;
import io.github.davidqf555.minecraft.beams.common.items.TargetingModuleItem;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TurretTileEntity extends OmnidirectionalProjectorTileEntity {

    private static final double START_DIST = 0.2;
    private static final double STOP_DIST = 0.5;
    private static final float ANGULAR_SPEED = (float) (Math.PI / 20);
    protected final NonNullList<ItemStack> targeting = NonNullList.withSize(3, ItemStack.EMPTY);

    protected TurretTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TurretTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.TURRET.get(), pos, state);
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
                        Vec3 target = ((TargetingModuleItem) item).getType().tick(this, ServerConfigs.INSTANCE.projectorMaxRange.get());
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
                                rotateTowards(target.subtract(Vec3.atCenterOf(pos)).normalize());
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

    private void rotateTowards(Vec3 direction) {
        Vec3 current = getDirection();
        Vec3 comp = current.cross(direction).cross(current);
        setDirection(current.scale(Mth.cos(ANGULAR_SPEED)).add(comp.scale(Mth.sin(ANGULAR_SPEED))));
    }

    protected boolean shouldStart(Vec3 target) {
        return getBeamDistanceSqr(target) <= START_DIST * START_DIST;
    }

    protected boolean shouldStop(Vec3 target) {
        return getBeamDistanceSqr(target) >= STOP_DIST * STOP_DIST;
    }

    private double getBeamDistanceSqr(Vec3 target) {
        Vec3 current = getDirection();
        Vec3 expected = target.subtract(Vec3.atCenterOf(getBlockPos()));
        double dot = current.dot(expected);
        return dot <= 0 ? Double.POSITIVE_INFINITY : current.scale(expected.lengthSqr() / dot).subtract(expected).lengthSqr();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
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
        ItemStack stack = ContainerHelper.removeItem(targeting, index - dif, amount);
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
        return ContainerHelper.takeItem(targeting, index - dif);
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
