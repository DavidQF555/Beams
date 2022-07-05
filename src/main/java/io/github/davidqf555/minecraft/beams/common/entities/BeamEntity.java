package io.github.davidqf555.minecraft.beams.common.entities;

import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.ProjectorModuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.*;

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
    private final Map<ProjectorModuleType, Integer> modules;
    private UUID shooter;
    private AABB bounds;
    private int lifespan;

    public BeamEntity(EntityType<? extends BeamEntity> type, Level world) {
        super(type, world);
        modules = new HashMap<>();
    }

    public static <T extends BeamEntity> List<T> shoot(EntityType<T> type, Level world, Vec3 start, Vec3 dir, double range, Map<ProjectorModuleType, Integer> modules, double poke, double baseStartWidth, double baseStartHeight, double baseMaxWidth, double baseMaxHeight) {
        Vec3 end = world.clip(new ClipContext(start, start.add(dir.scale(range)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null)).getLocation().add(dir.scale(poke));
        double endSizeFactor = getEndSizeFactor(modules);
        baseMaxWidth *= endSizeFactor;
        baseMaxHeight *= endSizeFactor;
        double startSizeFactor = getStartSizeFactor(modules);
        baseStartWidth *= startSizeFactor;
        baseStartHeight *= startSizeFactor;
        double distFactor = end.subtract(start).length() / range;
        return shoot(type, world, start, end, modules, baseStartWidth, baseStartHeight, baseStartWidth + (baseMaxWidth - baseStartWidth) * distFactor, baseStartHeight + (baseMaxHeight - baseStartHeight) * distFactor);
    }

    private static double getEndSizeFactor(Map<ProjectorModuleType, Integer> modules) {
        double factor = 1;
        for (ProjectorModuleType type : modules.keySet()) {
            factor *= type.getEndSizeFactor(modules.get(type));
        }
        return factor;
    }

    private static double getStartSizeFactor(Map<ProjectorModuleType, Integer> modules) {
        double factor = 1;
        for (ProjectorModuleType type : modules.keySet()) {
            factor *= type.getStartSizeFactor(modules.get(type));
        }
        return factor;
    }

    public static <T extends BeamEntity> List<T> shoot(EntityType<T> type, Level world, Vec3 start, Vec3 end, Map<ProjectorModuleType, Integer> modules, double startWidth, double startHeight, double endWidth, double endHeight) {
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
                entity.setModules(modules);
                double endDist = total - remaining;
                double startFactor = (endDist - length) / total;
                entity.setStartWidth(startWidth + (endWidth - startWidth) * startFactor);
                entity.setStartHeight(startHeight + (endHeight - startHeight) * startFactor);
                double endFactor = endDist / total;
                entity.setEndWidth(startWidth + (endWidth - startWidth) * endFactor);
                entity.setEndHeight(startHeight + (endHeight - startHeight) * endFactor);
                entity.initializeModules();
                world.addFreshEntity(entity);
                all.add(entity);
            }
            start = endPos;
        }
        return all;
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel) {
            int lifespan = getLifespan();
            if (lifespan > 0 && tickCount >= lifespan) {
                remove(RemovalReason.DISCARDED);
            } else {
                Map<ProjectorModuleType, Integer> modules = getModules();
                AABB bounds = getMaxBounds();
                for (int x = Mth.floor(bounds.minX); x <= Mth.floor(bounds.maxX); x++) {
                    for (int z = Mth.floor(bounds.minZ); z <= Mth.floor(bounds.maxZ); z++) {
                        for (int y = Mth.floor(bounds.minY); y <= Mth.floor(bounds.maxY); y++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            if (isAffected(pos)) {
                                modules.forEach((type, amt) -> {
                                    if (amt > 0) {
                                        type.onBlockTick(this, pos, amt);
                                    }
                                });
                                if (isColliding(pos)) {
                                    modules.forEach((type, amt) -> {
                                        if (amt > 0) {
                                            type.onCollisionTick(this, pos, amt);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
                for (Entity entity : level.getEntities(this, bounds)) {
                    if (isAffected(entity)) {
                        modules.forEach((type, amt) -> {
                            if (amt > 0) {
                                type.onEntityTick(this, entity, amt);
                            }
                        });
                    }
                }
            }
        }
    }

    protected boolean isColliding(BlockPos pos) {
        for (AABB bounds : level.getBlockState(pos).getCollisionShape(level, pos).toAabbs()) {
            if (isAffected(bounds.move(pos))) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAffected(Entity entity) {
        return !entity.getUUID().equals(getShooter()) && isAffected(entity.getBoundingBox());
    }

    protected boolean isAffected(BlockPos pos) {
        return isAffected(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos)));
    }

    protected boolean isAffected(Vec3 pos) {
        Vec3 start = getStart();
        Vec3 center = position().subtract(start);
        Vec3 dir = pos.subtract(start);
        double factor = center.dot(dir) / center.lengthSqr();
        if (factor <= 0 || factor > 1) {
            return false;
        }
        Vec3 proj = center.scale(factor);
        Vec3 dist = dir.subtract(proj);
        double startWidth = getStartWidth();
        double maxWidth = (startWidth + factor * (getEndWidth() - startWidth)) / 2;
        double startHeight = getStartHeight();
        double maxHeight = (startHeight + factor * (getEndHeight() - startHeight)) / 2;
        Vec3 cross = center.cross(new Vec3(Vector3f.YP));
        if (cross.lengthSqr() == 0) {
            return Math.abs(dist.z()) <= maxWidth && Math.abs(dist.x()) <= maxHeight;
        } else {
            Vec3 horizontal = cross.scale(cross.dot(dist) / cross.lengthSqr());
            Vec3 vertical = dist.subtract(horizontal);
            return horizontal.lengthSqr() <= maxWidth * maxWidth && vertical.lengthSqr() <= maxHeight * maxHeight;
        }
    }

    //not completely accurate, beam may hit bounding box with cross-section size less than the smallest beam dimension
    private boolean isAffected(AABB bounds) {
        double min = Math.min(Math.min(Math.min(getStartWidth(), getStartHeight()), getEndWidth()), getEndHeight());
        if (min == 0) {
            min = 0.1;
        }
        int xCount = Mth.ceil((bounds.maxX - bounds.minX) / min);
        int yCount = Mth.ceil((bounds.maxY - bounds.minY) / min);
        int zCount = Mth.ceil((bounds.maxZ - bounds.minZ) / min);
        for (int x = 0; x <= xCount; x++) {
            for (int z = 0; z <= zCount; z++) {
                for (int y = 0; y <= yCount; y++) {
                    Vec3 pos = new Vec3(bounds.minX + (bounds.maxX - bounds.minX) * x / xCount, bounds.minY + (bounds.maxY - bounds.minY) * y / yCount, bounds.minZ + (bounds.maxZ - bounds.minZ) * z / zCount);
                    if (isAffected(pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Map<ProjectorModuleType, Integer> getModules() {
        return modules;
    }

    protected void setModules(Map<ProjectorModuleType, Integer> modules) {
        this.modules.clear();
        this.modules.putAll(modules);
    }

    protected void initializeModules() {
        getModules().forEach((module, amt) -> {
            module.onStart(this, amt);
        });
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

    @Nullable
    public UUID getShooter() {
        return shooter;
    }

    public void setShooter(@Nullable UUID shooter) {
        this.shooter = shooter;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    private Vec3[] getVertices() {
        Vec3[] vertices = new Vec3[8];
        Vec3 start = getStart();
        Vec3 end = position();
        Vec3 center = end.subtract(start);
        Vec3 perpY = center.cross(new Vec3(Vector3f.YP)).normalize();
        if (perpY.lengthSqr() == 0) {
            perpY = new Vec3(Vector3f.ZP);
        }
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
        manager.define(COLOR, 0x40FFFFFF);
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
        if (tag.contains("Lifespan", Tag.TAG_INT)) {
            setLifespan(tag.getInt("Lifespan"));
        }
        if (tag.contains("Shooter", Tag.TAG_INT_ARRAY)) {
            setShooter(tag.getUUID("Shooter"));
        }
        if (tag.contains("Modules", Tag.TAG_COMPOUND)) {
            Map<ProjectorModuleType, Integer> modules = new HashMap<>();
            IForgeRegistry<ProjectorModuleType> registry = ProjectorModuleRegistry.getRegistry();
            CompoundTag map = tag.getCompound("Modules");
            for (String key : map.getAllKeys()) {
                ProjectorModuleType type = registry.getValue(new ResourceLocation(key));
                if (type != null && map.contains(key, Tag.TAG_INT)) {
                    modules.put(type, map.getInt(key));
                }
            }
            setModules(modules);
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
        tag.putInt("Lifespan", getLifespan());
        UUID shooter = getShooter();
        if (shooter != null) {
            tag.putUUID("Shooter", shooter);
        }
        CompoundTag modules = new CompoundTag();
        IForgeRegistry<ProjectorModuleType> registry = ProjectorModuleRegistry.getRegistry();
        this.modules.forEach((type, amt) -> {
            modules.putInt(registry.getKey(type).toString(), amt);
        });
        tag.put("Modules", modules);
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
