package io.github.davidqf555.minecraft.beams.common.entities;

import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeamEntity extends Entity {

    private static final EntityDataAccessor<Double> X = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> Y = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> Z = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> END_WIDTH = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> END_HEIGHT = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> START_WIDTH = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Double> START_HEIGHT = SynchedEntityData.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LAYERS = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);
    private AABB bounds;

    public BeamEntity(EntityType<? extends BeamEntity> type, Level world) {
        super(type, world);
    }

    public static <T extends BeamEntity> List<T> shoot(EntityType<T> type, Level world, Vec3 start, Vec3 end, double startWidth, double startHeight, double endWidth, double endHeight) {
        List<T> all = new ArrayList<>();
        double segment = ServerConfigs.INSTANCE.beamSegmentLength.get();
        Vec3 center = end.subtract(start);
        double total = center.length();
        Vec3 unit = center.normalize();
        double remaining = total;
        while (remaining > 0) {
            double length;
            if (remaining < segment) {
                length = remaining;
                remaining = 0;
            } else {
                length = segment;
                remaining -= segment;
            }
            Vec3 endPos = start.add(unit.scale(length));
            T entity = type.create(world);
            if (entity != null) {
                entity.setStart(start);
                entity.setPos(endPos.x(), endPos.y(), endPos.z());
                double endDist = total - remaining;
                double startFactor = (endDist - length) / total;
                entity.setStartWidth(startWidth + (endWidth - startWidth) * startFactor);
                entity.setStartHeight(startHeight + (endHeight - startHeight) * startFactor);
                double endFactor = endDist / total;
                entity.setEndWidth(startWidth + (endWidth - startWidth) * endFactor);
                entity.setEndHeight(startHeight + (endHeight - startHeight) * endFactor);
                world.addFreshEntity(entity);
                all.add(entity);
            }
            start = endPos;
        }
        return all;
    }

    public Vec3 getStart() {
        SynchedEntityData manager = getEntityData();
        return new Vec3(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setStart(Vec3 start) {
        SynchedEntityData manager = getEntityData();
        manager.set(X, start.x());
        manager.set(Y, start.y());
        manager.set(Z, start.z());
        refreshMaxBounds();
    }

    public double getStartWidth() {
        return getEntityData().get(START_WIDTH);
    }

    public void setStartWidth(double width) {
        getEntityData().set(START_WIDTH, width);
        refreshMaxBounds();
    }

    public double getStartHeight() {
        return getEntityData().get(START_HEIGHT);
    }

    public void setStartHeight(double height) {
        getEntityData().set(START_HEIGHT, height);
        refreshMaxBounds();
    }

    public double getEndWidth() {
        return getEntityData().get(END_WIDTH);
    }

    public void setEndWidth(double width) {
        getEntityData().set(END_WIDTH, width);
        refreshMaxBounds();
    }

    public double getEndHeight() {
        return getEntityData().get(END_HEIGHT);
    }

    public void setEndHeight(double height) {
        getEntityData().set(END_HEIGHT, height);
        refreshMaxBounds();
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        refreshMaxBounds();
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

    private Vec3[] getVertices() {
        Vec3[] vertices = new Vec3[8];
        Vec3 start = getStart();
        Vec3 end = position();
        Vec3 center = end.subtract(start);
        Vec3 perpY = center.cross(new Vec3(Vector3f.YP)).normalize();
        Vec3 perp = center.cross(perpY).normalize();
        double startWidth = getStartWidth();
        double startHeight = getStartHeight();
        vertices[0] = start.add(perpY.scale(startWidth / 2)).add(perp.scale(startHeight / 2));
        vertices[1] = start.add(perpY.scale(startWidth / 2)).subtract(perp.scale(startHeight / 2));
        vertices[2] = start.subtract(perpY.scale(startWidth / 2)).add(perp.scale(startHeight / 2));
        vertices[3] = start.subtract(perpY.scale(startWidth / 2)).subtract(perp.scale(startHeight / 2));
        double endWidth = getEndWidth();
        double endHeight = getEndHeight();
        vertices[4] = end.add(perpY.scale(endWidth / 2)).add(perp.scale(endHeight / 2));
        vertices[5] = end.add(perpY.scale(endWidth / 2)).subtract(perp.scale(endHeight / 2));
        vertices[6] = end.subtract(perpY.scale(endWidth / 2)).add(perp.scale(endHeight / 2));
        vertices[7] = end.subtract(perpY.scale(endWidth / 2)).subtract(perp.scale(endHeight / 2));
        return vertices;
    }

    public AABB getMaxBounds() {
        if (bounds == null) {
            Vec3[] vertices = getVertices();
            double minX = Arrays.stream(vertices).mapToDouble(Vec3::x).min().getAsDouble();
            double maxX = Arrays.stream(vertices).mapToDouble(Vec3::x).max().getAsDouble();
            double minY = Arrays.stream(vertices).mapToDouble(Vec3::y).min().getAsDouble();
            double maxY = Arrays.stream(vertices).mapToDouble(Vec3::y).max().getAsDouble();
            double minZ = Arrays.stream(vertices).mapToDouble(Vec3::z).min().getAsDouble();
            double maxZ = Arrays.stream(vertices).mapToDouble(Vec3::z).max().getAsDouble();
            bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return bounds;
    }

    protected void refreshMaxBounds() {
        bounds = null;
    }

    @Override
    protected void defineSynchedData() {
        SynchedEntityData manager = getEntityData();
        manager.define(X, 0.0);
        manager.define(Y, 0.0);
        manager.define(Z, 0.0);
        manager.define(START_WIDTH, 1.0);
        manager.define(START_HEIGHT, 1.0);
        manager.define(END_WIDTH, 1.0);
        manager.define(END_HEIGHT, 1.0);
        manager.define(COLOR, 0x80FFFFFF);
        manager.define(LAYERS, 1);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return getMaxBounds();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("StartX", Tag.TAG_DOUBLE) && tag.contains("StartY", Tag.TAG_DOUBLE) && tag.contains("StartZ", Tag.TAG_DOUBLE)) {
            setStart(new Vec3(tag.getDouble("StartX"), tag.getDouble("StartY"), tag.getDouble("StartZ")));
        }
        if (tag.contains("StartWidth", Tag.TAG_DOUBLE)) {
            setStartWidth(tag.getDouble("StartWidth"));
        }
        if (tag.contains("StartHeight", Tag.TAG_DOUBLE)) {
            setStartHeight(tag.getDouble("StartHeight"));
        }
        if (tag.contains("EndWidth", Tag.TAG_DOUBLE)) {
            setEndWidth(tag.getDouble("EndWidth"));
        }
        if (tag.contains("EndHeight", Tag.TAG_DOUBLE)) {
            setEndHeight(tag.getDouble("EndHeight"));
        }
        if (tag.contains("Color", Tag.TAG_INT)) {
            setColor(tag.getInt("Color"));
        }
        if (tag.contains("Layers", Tag.TAG_INT)) {
            setLayers(tag.getInt("Layers"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Vec3 start = getStart();
        tag.putDouble("StartX", start.x());
        tag.putDouble("StartY", start.y());
        tag.putDouble("StartZ", start.z());
        tag.putDouble("StartWidth", getStartWidth());
        tag.putDouble("StartHeight", getStartHeight());
        tag.putDouble("EndWidth", getEndWidth());
        tag.putDouble("EndHeight", getEndHeight());
        tag.putInt("Color", getColor());
        tag.putInt("Layers", getLayers());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distSq) {
        double range = 64 * getViewScale();
        return distSq < range * range;
    }

}
