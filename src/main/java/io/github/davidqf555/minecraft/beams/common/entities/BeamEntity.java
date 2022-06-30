package io.github.davidqf555.minecraft.beams.common.entities;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.ProjectorModuleRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.*;

public class BeamEntity extends Entity {

    private static final DataParameter<Double> X = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> Y = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> Z = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> END_WIDTH = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> END_HEIGHT = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> START_WIDTH = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> START_HEIGHT = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(BeamEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> LAYERS = EntityDataManager.defineId(BeamEntity.class, DataSerializers.INT);
    private final Map<ProjectorModuleType, Integer> modules;
    private UUID shooter;
    private AxisAlignedBB bounds;
    private int lifespan;

    public BeamEntity(EntityType<? extends BeamEntity> type, World world) {
        super(type, world);
        modules = new HashMap<>();
    }

    public static <T extends BeamEntity> List<T> shoot(EntityType<T> type, World world, Vector3d start, Vector3d dir, double range, Map<ProjectorModuleType, Integer> modules, double poke, double startWidth, double startHeight, double maxWidth, double maxHeight) {
        Vector3d end = world.clip(new RayTraceContext(start, start.add(dir.scale(range)), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getLocation().add(dir.scale(poke));
        double endFactor = end.subtract(start).length() / range;
        return shoot(type, world, start, end, modules, startWidth, startHeight, startWidth + (maxWidth - startWidth) * endFactor, startHeight + (maxHeight - startHeight) * endFactor);
    }

    public static <T extends BeamEntity> List<T> shoot(EntityType<T> type, World world, Vector3d start, Vector3d end, Map<ProjectorModuleType, Integer> modules, double startWidth, double startHeight, double endWidth, double endHeight) {
        List<T> all = new ArrayList<>();
        double segment = ServerConfigs.INSTANCE.beamSegmentLength.get();
        Vector3d center = end.subtract(start);
        double total = center.length();
        Vector3d unit = center.normalize();
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
            Vector3d endPos = start.add(unit.scale(length));
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
                entity.setModules(modules);
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
        if (level instanceof ServerWorld) {
            int lifespan = getLifespan();
            if (lifespan > 0 && tickCount >= lifespan) {
                remove();
            } else {
                Map<ProjectorModuleType, Integer> modules = getModules();
                AxisAlignedBB bounds = getMaxBounds();
                for (int x = MathHelper.floor(bounds.minX); x <= MathHelper.floor(bounds.maxX); x++) {
                    for (int z = MathHelper.floor(bounds.minZ); z <= MathHelper.floor(bounds.maxZ); z++) {
                        for (int y = MathHelper.floor(bounds.minY); y <= MathHelper.floor(bounds.maxY); y++) {
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
        for (AxisAlignedBB bounds : level.getBlockState(pos).getCollisionShape(level, pos).toAabbs()) {
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
        return isAffected(AxisAlignedBB.unitCubeFromLowerCorner(Vector3d.atLowerCornerOf(pos)));
    }

    protected boolean isAffected(Vector3d pos) {
        Vector3d start = getStart();
        Vector3d center = position().subtract(start);
        Vector3d dir = pos.subtract(start);
        double factor = center.dot(dir) / center.lengthSqr();
        if (factor <= 0 || factor > 1) {
            return false;
        }
        Vector3d proj = center.scale(factor);
        Vector3d dist = dir.subtract(proj);
        double startWidth = getStartWidth();
        double maxWidth = (startWidth + factor * (getEndWidth() - startWidth)) / 2;
        double startHeight = getStartHeight();
        double maxHeight = (startHeight + factor * (getEndHeight() - startHeight)) / 2;
        Vector3d cross = center.cross(new Vector3d(Vector3f.YP));
        if (cross.lengthSqr() == 0) {
            return Math.abs(dist.z()) <= maxWidth && Math.abs(dist.x()) <= maxHeight;
        } else {
            Vector3d horizontal = cross.scale(cross.dot(dist) / cross.lengthSqr());
            Vector3d vertical = dist.subtract(horizontal);
            return horizontal.lengthSqr() <= maxWidth * maxWidth && vertical.lengthSqr() <= maxHeight * maxHeight;
        }
    }

    //not completely accurate, beam may hit bounding box with cross-section size less than the smallest beam dimension
    protected boolean isAffected(AxisAlignedBB bounds) {
        double min = Math.min(Math.min(Math.min(getStartWidth(), getStartHeight()), getEndWidth()), getEndHeight());
        if (min == 0) {
            min = 0.1;
        }
        int xCount = MathHelper.ceil((bounds.maxX - bounds.minX) / min);
        int yCount = MathHelper.ceil((bounds.maxY - bounds.minY) / min);
        int zCount = MathHelper.ceil((bounds.maxZ - bounds.minZ) / min);
        for (int x = 0; x <= xCount; x++) {
            for (int z = 0; z <= zCount; z++) {
                for (int y = 0; y <= yCount; y++) {
                    Vector3d pos = new Vector3d(bounds.minX + (bounds.maxX - bounds.minX) * x / xCount, bounds.minY + (bounds.maxY - bounds.minY) * y / yCount, bounds.minZ + (bounds.maxZ - bounds.minZ) * z / zCount);
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

    public void setModules(Map<ProjectorModuleType, Integer> modules) {
        this.modules.clear();
        this.modules.putAll(modules);
        this.modules.forEach((type, amt) -> {
            if (amt > 0) {
                type.onStart(this, amt);
            }
        });
    }

    public Vector3d getStart() {
        EntityDataManager manager = getEntityData();
        return new Vector3d(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setStart(Vector3d start) {
        EntityDataManager manager = getEntityData();
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

    private Vector3d[] getVertices() {
        Vector3d[] vertices = new Vector3d[8];
        Vector3d start = getStart();
        Vector3d end = position();
        Vector3d center = end.subtract(start);
        Vector3d perpY = center.cross(new Vector3d(Vector3f.YP)).normalize();
        if (perpY.lengthSqr() == 0) {
            perpY = new Vector3d(Vector3f.ZP);
        }
        Vector3d perp = center.cross(perpY).normalize();
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

    public AxisAlignedBB getMaxBounds() {
        if (bounds == null) {
            Vector3d[] vertices = getVertices();
            double minX = Arrays.stream(vertices).mapToDouble(Vector3d::x).min().getAsDouble();
            double maxX = Arrays.stream(vertices).mapToDouble(Vector3d::x).max().getAsDouble();
            double minY = Arrays.stream(vertices).mapToDouble(Vector3d::y).min().getAsDouble();
            double maxY = Arrays.stream(vertices).mapToDouble(Vector3d::y).max().getAsDouble();
            double minZ = Arrays.stream(vertices).mapToDouble(Vector3d::z).min().getAsDouble();
            double maxZ = Arrays.stream(vertices).mapToDouble(Vector3d::z).max().getAsDouble();
            bounds = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return bounds;
    }

    protected void refreshMaxBounds() {
        bounds = null;
    }

    @Override
    protected void defineSynchedData() {
        EntityDataManager manager = getEntityData();
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
    public AxisAlignedBB getBoundingBoxForCulling() {
        return getMaxBounds();
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        if (tag.contains("StartX", Constants.NBT.TAG_DOUBLE) && tag.contains("StartY", Constants.NBT.TAG_DOUBLE) && tag.contains("StartZ", Constants.NBT.TAG_DOUBLE)) {
            setStart(new Vector3d(tag.getDouble("StartX"), tag.getDouble("StartY"), tag.getDouble("StartZ")));
        }
        if (tag.contains("StartWidth", Constants.NBT.TAG_DOUBLE)) {
            setStartWidth(tag.getDouble("StartWidth"));
        }
        if (tag.contains("StartHeight", Constants.NBT.TAG_DOUBLE)) {
            setStartHeight(tag.getDouble("StartHeight"));
        }
        if (tag.contains("EndWidth", Constants.NBT.TAG_DOUBLE)) {
            setEndWidth(tag.getDouble("EndWidth"));
        }
        if (tag.contains("EndHeight", Constants.NBT.TAG_DOUBLE)) {
            setEndHeight(tag.getDouble("EndHeight"));
        }
        if (tag.contains("Lifespan", Constants.NBT.TAG_INT)) {
            setLifespan(tag.getInt("Lifespan"));
        }
        if (tag.contains("Shooter", Constants.NBT.TAG_INT_ARRAY)) {
            setShooter(tag.getUUID("Shooter"));
        }
        if (tag.contains("Modules", Constants.NBT.TAG_COMPOUND)) {
            Map<ProjectorModuleType, Integer> modules = new HashMap<>();
            IForgeRegistry<ProjectorModuleType> registry = ProjectorModuleRegistry.getRegistry();
            CompoundNBT map = tag.getCompound("Modules");
            for (String key : map.getAllKeys()) {
                ProjectorModuleType type = registry.getValue(new ResourceLocation(key));
                if (type != null && map.contains(key, Constants.NBT.TAG_INT)) {
                    modules.put(type, map.getInt(key));
                }
            }
            setModules(modules);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
        Vector3d start = getStart();
        tag.putDouble("StartX", start.x());
        tag.putDouble("StartY", start.y());
        tag.putDouble("StartZ", start.z());
        tag.putDouble("StartWidth", getStartWidth());
        tag.putDouble("StartHeight", getStartHeight());
        tag.putDouble("EndWidth", getEndWidth());
        tag.putDouble("EndHeight", getEndHeight());
        tag.putInt("Lifespan", getLifespan());
        UUID shooter = getShooter();
        if (shooter != null) {
            tag.putUUID("Shooter", shooter);
        }
        CompoundNBT modules = new CompoundNBT();
        this.modules.forEach((type, amt) -> {
            modules.putInt(type.getRegistryName().toString(), amt);
        });
        tag.put("Modules", modules);
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
