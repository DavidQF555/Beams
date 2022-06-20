package io.github.davidqf555.minecraft.beams.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class BeamEntity extends Entity {

    private static final double SEGMENT_LENGTH = 16;
    private static final DataParameter<Float> X = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> Y = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> Z = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> END_WIDTH = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> END_HEIGHT = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> START_WIDTH = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> START_HEIGHT = EntityDataManager.defineId(BeamEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(BeamEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> LAYERS = EntityDataManager.defineId(BeamEntity.class, DataSerializers.INT);
    private double maxRange;

    public BeamEntity(EntityType<? extends BeamEntity> type, World world) {
        super(type, world);
    }

    public static <T extends BeamEntity> List<T> shoot(EntityType<T> type, World world, Vector3d start, Vector3d end, float startWidth, float startHeight, float endWidth, float endHeight, double range) {
        List<T> all = new ArrayList<>();
        Vector3d center = end.subtract(start);
        double total = center.length();
        Vector3d unit = center.normalize();
        double remaining = total;
        while (remaining > 0) {
            double length;
            if (remaining < SEGMENT_LENGTH) {
                length = remaining;
                remaining = 0;
            } else {
                length = SEGMENT_LENGTH;
                remaining -= SEGMENT_LENGTH;
            }
            Vector3d endPos = start.add(unit.scale(length));
            T entity = type.create(world);
            if (entity != null) {
                entity.setStart(new Vector3f(start));
                entity.setPos(endPos.x(), endPos.y(), endPos.z());
                double endDist = total - remaining;
                float startFactor = (float) ((endDist - length) / total);
                entity.setStartWidth(startWidth + (endWidth - startWidth) * startFactor);
                entity.setStartHeight(startHeight + (endHeight - startHeight) * startFactor);
                float endFactor = (float) (endDist / total);
                entity.setEndWidth(startWidth + (endWidth - startWidth) * endFactor);
                entity.setEndHeight(startHeight + (endHeight - startHeight) * endFactor);
                entity.setMaxRange(range);
                world.addFreshEntity(entity);
                all.add(entity);
            }
            start = endPos;
        }
        return all;
    }

    public Vector3f getStart() {
        EntityDataManager manager = getEntityData();
        return new Vector3f(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setStart(Vector3f start) {
        EntityDataManager manager = getEntityData();
        manager.set(X, start.x());
        manager.set(Y, start.y());
        manager.set(Z, start.z());
    }

    public float getStartWidth() {
        return getEntityData().get(START_WIDTH);
    }

    public void setStartWidth(float width) {
        getEntityData().set(START_WIDTH, width);
    }

    public float getStartHeight() {
        return getEntityData().get(START_HEIGHT);
    }

    public void setStartHeight(float height) {
        getEntityData().set(START_HEIGHT, height);
    }

    public float getEndWidth() {
        return getEntityData().get(END_WIDTH);
    }

    public void setEndWidth(float width) {
        getEntityData().set(END_WIDTH, width);
    }

    public float getEndHeight() {
        return getEntityData().get(END_HEIGHT);
    }

    public void setEndHeight(float height) {
        getEntityData().set(END_HEIGHT, height);
    }

    public int getColor() {
        return getEntityData().get(COLOR);
    }

    public void setColor(int color) {
        getEntityData().set(COLOR, color);
    }

    public int getLayers() {
        return getEntityData().get(LAYERS);
    }

    public void setLayers(int layers) {
        getEntityData().set(LAYERS, layers);
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide()) {
            Vector3d start = new Vector3d(getStart());
            Vector3d line = position().subtract(start);
            Vector3d clip = level.clip(new RayTraceContext(start, start.add(line.normalize().scale(getMaxRange())), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, null)).getLocation();
            setPos(clip.x(), clip.y(), clip.z());
        }
    }

    @Override
    protected void defineSynchedData() {
        EntityDataManager manager = getEntityData();
        manager.define(X, 0f);
        manager.define(Y, 0f);
        manager.define(Z, 0f);
        manager.define(START_WIDTH, 1f);
        manager.define(START_HEIGHT, 1f);
        manager.define(END_WIDTH, 1f);
        manager.define(END_HEIGHT, 1f);
        manager.define(COLOR, 0xFFFFFFFF);
        manager.define(LAYERS, 1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        if (tag.contains("StartX", Constants.NBT.TAG_FLOAT) && tag.contains("StartY", Constants.NBT.TAG_FLOAT) && tag.contains("StartZ", Constants.NBT.TAG_FLOAT)) {
            setStart(new Vector3f(tag.getFloat("StartX"), tag.getFloat("StartY"), tag.getFloat("StartZ")));
        }
        if (tag.contains("StartWidth", Constants.NBT.TAG_FLOAT)) {
            setStartWidth(tag.getFloat("StartWidth"));
        }
        if (tag.contains("StartHeight", Constants.NBT.TAG_FLOAT)) {
            setStartHeight(tag.getFloat("StartHeight"));
        }
        if (tag.contains("EndWidth", Constants.NBT.TAG_FLOAT)) {
            setEndWidth(tag.getFloat("EndWidth"));
        }
        if (tag.contains("EndHeight", Constants.NBT.TAG_FLOAT)) {
            setEndHeight(tag.getFloat("EndHeight"));
        }
        if (tag.contains("Color", Constants.NBT.TAG_INT)) {
            setColor(tag.getInt("Color"));
        }
        if (tag.contains("Layers", Constants.NBT.TAG_INT)) {
            setLayers(tag.getInt("Layers"));
        }
        if (tag.contains("MaxRange", Constants.NBT.TAG_DOUBLE)) {
            setMaxRange(tag.getDouble("MaxRange"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
        Vector3f start = getStart();
        tag.putFloat("StartX", start.x());
        tag.putFloat("StartY", start.y());
        tag.putFloat("StartZ", start.z());
        tag.putFloat("StartWidth", getStartWidth());
        tag.putFloat("StartHeight", getStartHeight());
        tag.putFloat("EndWidth", getEndWidth());
        tag.putFloat("EndHeight", getEndHeight());
        tag.putInt("Color", getColor());
        tag.putInt("Layers", getLayers());
        tag.putDouble("MaxRange", getMaxRange());
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distSq) {
        double range = 64 * getViewScale();
        return distSq < range * range;
    }

}
