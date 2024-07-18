package io.github.davidqf555.minecraft.beams.common.entities;

import io.github.davidqf555.minecraft.beams.common.blocks.IBeamAffectEffect;
import io.github.davidqf555.minecraft.beams.common.blocks.IBeamCollisionEffect;
import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityDataSerializerRegistry;
import io.github.davidqf555.minecraft.beams.registration.ProjectorModuleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BeamEntity extends Entity {

    public static final double POKE = 0.1;
    private static final EntityDataAccessor<Double> X = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Double> Y = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Double> Z = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Double> END_WIDTH = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Double> END_HEIGHT = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Double> START_WIDTH = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Double> START_HEIGHT = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializerRegistry.DOUBLE);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LAYERS = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.INT);
    private final Map<ProjectorModuleType, Integer> modules = new HashMap<>();
    private BlockPos projector;
    private double maxRange;
    private UUID shooter, parent;
    private int lifespan;
    private Cuboid shape;

    public BeamEntity(EntityType<? extends BeamEntity> type, Level world) {
        super(type, world);
    }

    @Nullable
    public static <T extends BeamEntity> T shoot(EntityType<T> type, Level world, Vec3 start, Vec3 dir, double range, Map<ProjectorModuleType, Integer> modules, double baseWidth, double baseHeight, @Nullable UUID parent, @Nullable BlockPos projector) {
        T beam = type.create(world);
        if (beam != null) {
            beam.setDirectParent(parent);
            Vec3 end = world.clip(new ClipContext(start, start.add(dir.scale(range)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation().add(dir.scale(POKE));
            double startFactor = getStartSizeFactor(modules);
            double startWidth = baseWidth * startFactor;
            double startHeight = baseHeight * startFactor;
            beam.setPos(start.x(), start.y(), start.z());
            beam.setEnd(end, true, false);
            beam.setModules(modules);
            beam.setStartWidth(startWidth);
            beam.setStartHeight(startHeight);
            double growthRate = getGrowthRate(modules);
            double length = end.subtract(start).length();
            beam.setEndWidth(startWidth + growthRate * length);
            beam.setEndHeight(startHeight + growthRate * length);
            beam.setMaxRange(range);
            beam.setProjectorPos(projector);
            beam.initializeModules();
            world.addFreshEntity(beam);
            return beam;
        }
        return null;
    }

    private static double getGrowthRate(Map<ProjectorModuleType, Integer> modules) {
        double rate = 0;
        for (ProjectorModuleType type : modules.keySet()) {
            rate += type.getGrowthRate(modules.get(type));
        }
        return rate;
    }

    private static double getStartSizeFactor(Map<ProjectorModuleType, Integer> modules) {
        double factor = 1;
        for (ProjectorModuleType type : modules.keySet()) {
            factor *= type.getStartSizeFactor(modules.get(type));
        }
        return factor;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    protected boolean isSignificantlyDifferent(Vec3 v1, Vec3 v2) {
        return v1.distanceToSqr(v2) >= 0.00001;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        if (level instanceof ServerLevel) {
            BlockPos projector = getProjectorPos();
            int lifespan = getLifespan();
            if (lifespan > 0) {
                if (tickCount >= lifespan) {
                    discard();
                    return;
                }
            } else if (projector == null) {
                discard();
                return;
            } else {
                BlockEntity te = level.getBlockEntity(projector);
                if (!(te instanceof AbstractProjectorTileEntity) || !((AbstractProjectorTileEntity) te).getBeams().contains(getUUID())) {
                    discard();
                    return;
                }
            }
            Vec3 start = position();
            Vec3 original = getEnd();
            Vec3 dir = original.subtract(start).normalize();
            BlockHitResult trace = level.clip(new ClipContext(start, start.add(dir.scale(maxRange)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
            Vec3 end = trace.getLocation().add(dir.scale(POKE));
            if (isSignificantlyDifferent(original, end)) {
                setEnd(end, true, true);
                original = end;
            }
            BlockPos endPos = BlockPos.containing(original);
            BlockState endState = level.getBlockState(endPos);
            Block endBlock = endState.getBlock();
            if (endBlock instanceof IBeamCollisionEffect) {
                ((IBeamCollisionEffect) endBlock).onBeamCollisionTick(this, endPos, endState);
            }
            Cuboid shape = getShape();
            Set<Map.Entry<ProjectorModuleType, Integer>> blockModules = modules.entrySet().stream().filter(entry -> entry.getValue() > 0 && entry.getKey().shouldTickBlocks()).collect(Collectors.toSet());
            shape.doBlockEffect(pos -> {
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();
                if (block instanceof IBeamAffectEffect) {
                    ((IBeamAffectEffect) block).onBeamAffectTick(this, pos, state);
                }
                blockModules.forEach(entry -> {
                    ProjectorModuleType type = entry.getKey();
                    int count = entry.getValue();
                    type.onBlockTick(this, pos, count);
                    if (isVisualColliding(pos, state)) {
                        type.onCollisionTick(this, pos, count);
                    }
                });
            });
            Set<Map.Entry<ProjectorModuleType, Integer>> entities = modules.entrySet().stream().filter(entry -> entry.getValue() > 0 && entry.getKey().shouldTickEntities()).collect(Collectors.toSet());
            if (!entities.isEmpty()) {
                for (Entity entity : level.getEntities(this, shape.getBounds())) {
                    if (isColliding(entity)) {
                        entities.forEach(entry -> entry.getKey().onEntityTick(this, entity, entry.getValue()));
                    }
                }
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        Vec3 end = getEnd();
        BlockPos endPos = new BlockPos((int) end.x(), (int) end.y(), (int) end.z());
        BlockState endState = level().getBlockState(endPos);
        Block endBlock = endState.getBlock();
        if (endBlock instanceof IBeamCollisionEffect) {
            ((IBeamCollisionEffect) endBlock).onBeamStopCollision(this, endPos, endState);

        }
        getShape().doBlockEffect(pos -> {
            BlockState state = level().getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof IBeamAffectEffect) {
                ((IBeamAffectEffect) block).onBeamStopAffect(this, pos, state);
            }
        });
        super.remove(reason);
    }

    protected boolean isVisualColliding(BlockPos pos, BlockState state) {
        Cuboid shape = getShape();
        for (AABB bounds : state.getVisualShape(level(), pos, CollisionContext.empty()).toAabbs()) {
            if (shape.isColliding(bounds.move(pos))) {
                return true;
            }
        }
        return false;
    }


    protected boolean isColliding(Entity entity) {
        return getShape().isColliding(entity.getBoundingBox());
    }

    @Nullable
    public UUID getDirectParent() {
        return parent;
    }

    public void setDirectParent(@Nullable UUID parent) {
        this.parent = parent;
    }

    public Set<UUID> getParents() {
        Set<UUID> parents = new HashSet<>();
        UUID direct = getDirectParent();
        if (direct != null) {
            parents.add(direct);
            if (level() instanceof ServerLevel) {
                Entity parent = ((ServerLevel) level()).getEntity(direct);
                if (parent instanceof BeamEntity) {
                    parents.addAll(((BeamEntity) parent).getParents());
                }
            }
        }
        return parents;
    }

    @Nullable
    public BlockPos getProjectorPos() {
        return projector;
    }

    public void setProjectorPos(@Nullable BlockPos projector) {
        this.projector = projector;
    }

    public Map<ProjectorModuleType, Integer> getModules() {
        return modules;
    }

    protected void setModules(Map<ProjectorModuleType, Integer> modules) {
        this.modules.clear();
        this.modules.putAll(modules);
    }

    protected void initializeModules() {
        getModules().forEach((module, amt) -> module.onStart(this, amt));
    }

    public Vec3 getEnd() {
        SynchedEntityData manager = getEntityData();
        return new Vec3(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setEnd(Vec3 end, boolean start, boolean stop) {
        Vec3 before = getEnd();
        if (!end.equals(before)) {
            Cuboid shape = getShape();
            setEndRaw(end);
            if (stop) {
                BlockPos beforePos = BlockPos.containing(before);
                BlockState beforeState = level().getBlockState(beforePos);
                Block beforeBlock = beforeState.getBlock();
                if (beforeBlock instanceof IBeamCollisionEffect) {
                    ((IBeamCollisionEffect) beforeBlock).onBeamStopCollision(this, beforePos, beforeState);
                }
                shape.doBlockEffect(pos -> {
                    BlockState state = level().getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof IBeamAffectEffect) {
                        ((IBeamAffectEffect) block).onBeamStopAffect(this, pos, state);
                    }
                });
            }
            double length = end.subtract(position()).length();
            double growthRate = getGrowthRate(getModules());
            setEndWidth(getStartWidth() + growthRate * length);
            setEndHeight(getStartHeight() + growthRate * length);
            if (start) {
                BlockPos afterPos = BlockPos.containing(end);
                BlockState afterState = level().getBlockState(afterPos);
                Block afterBlock = afterState.getBlock();
                if (afterBlock instanceof IBeamCollisionEffect) {
                    ((IBeamCollisionEffect) afterBlock).onBeamStartCollision(this, afterPos, afterState);
                }
            }
        }
    }

    public void setEndRaw(Vec3 end) {
        SynchedEntityData manager = getEntityData();
        manager.set(X, end.x());
        manager.set(Y, end.y());
        manager.set(Z, end.z());
        refreshShape();
    }

    public double getStartWidth() {
        return getEntityData().get(START_WIDTH);
    }

    public void setStartWidth(double width) {
        getEntityData().set(START_WIDTH, width);
        refreshShape();
    }

    public double getStartHeight() {
        return getEntityData().get(START_HEIGHT);
    }

    public void setStartHeight(double height) {
        getEntityData().set(START_HEIGHT, height);
        refreshShape();
    }

    public double getEndWidth() {
        return getEntityData().get(END_WIDTH);
    }

    public void setEndWidth(double width) {
        getEntityData().set(END_WIDTH, width);
        refreshShape();
    }

    public double getEndHeight() {
        return getEntityData().get(END_HEIGHT);
    }

    public void setEndHeight(double height) {
        getEntityData().set(END_HEIGHT, height);
        refreshShape();
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        refreshShape();
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

    public Cuboid getShape() {
        if (shape == null) {
            shape = Cuboid.fromBeam(this);
        }
        return shape;
    }

    protected void refreshShape() {
        shape = null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(X, 0.0);
        builder.define(Y, 0.0);
        builder.define(Z, 0.0);
        builder.define(START_WIDTH, 1.0);
        builder.define(START_HEIGHT, 1.0);
        builder.define(END_WIDTH, 1.0);
        builder.define(END_HEIGHT, 1.0);
        builder.define(COLOR, 0x40FFFFFF);
        builder.define(LAYERS, 1);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return getShape().getBounds();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("EndX", Tag.TAG_DOUBLE) && tag.contains("EndY", Tag.TAG_DOUBLE) && tag.contains("EndZ", Tag.TAG_DOUBLE)) {
            setEndRaw(new Vec3(tag.getDouble("EndX"), tag.getDouble("EndY"), tag.getDouble("EndZ")));
        }
        if (tag.contains("ProjectorX", Tag.TAG_INT) && tag.contains("ProjectorY", Tag.TAG_INT) && tag.contains("ProjectorZ", Tag.TAG_INT)) {
            setProjectorPos(new BlockPos(tag.getInt("ProjectorX"), tag.getInt("ProjectorY"), tag.getInt("ProjectorZ")));
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
        if (tag.contains("Parent", Tag.TAG_INT_ARRAY)) {
            setDirectParent(tag.getUUID("Parent"));
        }
        if (tag.contains("Shooter", Tag.TAG_INT_ARRAY)) {
            setShooter(tag.getUUID("Shooter"));
        }
        if (tag.contains("MaxRange", Tag.TAG_DOUBLE)) {
            setMaxRange(tag.getDouble("MaxRange"));
        }
        if (tag.contains("Modules", Tag.TAG_COMPOUND)) {
            Map<ProjectorModuleType, Integer> modules = new HashMap<>();
            Registry<ProjectorModuleType> registry = ProjectorModuleRegistry.getRegistry();
            CompoundTag map = tag.getCompound("Modules");
            for (String key : map.getAllKeys()) {
                ProjectorModuleType type = registry.get(ResourceLocation.parse(key));
                if (type != null && map.contains(key, Tag.TAG_INT)) {
                    modules.put(type, map.getInt(key));
                }
            }
            setModules(modules);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Vec3 end = getEnd();
        tag.putDouble("EndX", end.x());
        tag.putDouble("EndY", end.y());
        tag.putDouble("EndZ", end.z());
        tag.putDouble("StartWidth", getStartWidth());
        tag.putDouble("StartHeight", getStartHeight());
        tag.putDouble("EndWidth", getEndWidth());
        tag.putDouble("EndHeight", getEndHeight());
        tag.putInt("Color", getColor());
        tag.putInt("Layers", getLayers());
        tag.putInt("Lifespan", getLifespan());
        tag.putDouble("MaxRange", getMaxRange());
        UUID parent = getDirectParent();
        if (parent != null) {
            tag.putUUID("Parent", parent);
        }
        UUID shooter = getShooter();
        if (shooter != null) {
            tag.putUUID("Shooter", shooter);
        }
        BlockPos projector = getProjectorPos();
        if (projector != null) {
            tag.putInt("ProjectorX", projector.getX());
            tag.putInt("ProjectorY", projector.getY());
            tag.putInt("ProjectorZ", projector.getZ());
        }
        CompoundTag modules = new CompoundTag();
        Registry<ProjectorModuleType> registry = ProjectorModuleRegistry.getRegistry();
        this.modules.forEach((type, amt) -> modules.putInt(registry.getKey(type).toString(), amt));
        tag.put("Modules", modules);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distSq) {
        double range = 64 * getViewScale();
        return distSq < range * range;
    }

}
