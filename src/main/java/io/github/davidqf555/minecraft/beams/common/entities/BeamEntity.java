package io.github.davidqf555.minecraft.beams.common.entities;

import io.github.davidqf555.minecraft.beams.common.blocks.IBeamAffectEffect;
import io.github.davidqf555.minecraft.beams.common.blocks.IBeamCollisionEffect;
import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.ProjectorModuleRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BeamEntity extends Entity {

    public static final double POKE = 0.1;
    private static final DataParameter<Double> X = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> Y = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> Z = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> END_WIDTH = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> END_HEIGHT = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> START_WIDTH = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Double> START_HEIGHT = EntityDataManager.defineId(BeamEntity.class, DoubleSerializer.INSTANCE);
    private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(BeamEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> LAYERS = EntityDataManager.defineId(BeamEntity.class, DataSerializers.INT);
    private final Map<ProjectorModuleType, Integer> modules = new HashMap<>();
    private BlockPos projector;
    private double maxRange;
    private UUID shooter, parent;
    private int lifespan;
    private Cuboid shape;

    public BeamEntity(EntityType<? extends BeamEntity> type, World world) {
        super(type, world);
    }

    @Nullable
    public static <T extends BeamEntity> T shoot(EntityType<T> type, World world, Vector3d start, Vector3d dir, double range, Map<ProjectorModuleType, Integer> modules, double baseWidth, double baseHeight, @Nullable UUID parent, @Nullable BlockPos projector) {
        T beam = type.create(world);
        if (beam != null) {
            beam.setDirectParent(parent);
            Vector3d end = world.clip(new RayTraceContext(start, start.add(dir.scale(range)), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getLocation().add(dir.scale(POKE));
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

    private static Vector3d[][] getVertices(Vector3d start, Vector3d end, double startWidth, double startHeight, double endWidth, double endHeight) {
        Vector3d[][] vertices = new Vector3d[2][4];
        Vector3d center = end.subtract(start);
        Vector3d perpY = center.cross(new Vector3d(Vector3f.YP)).normalize();
        if (perpY.lengthSqr() == 0) {
            perpY = new Vector3d(Vector3f.ZP);
        }
        Vector3d perp = center.cross(perpY).normalize();
        vertices[0][0] = start.add(perpY.scale(startWidth / 2)).add(perp.scale(startHeight / 2));
        vertices[0][1] = start.add(perpY.scale(startWidth / 2)).subtract(perp.scale(startHeight / 2));
        vertices[0][2] = start.subtract(perpY.scale(startWidth / 2)).subtract(perp.scale(startHeight / 2));
        vertices[0][3] = start.subtract(perpY.scale(startWidth / 2)).add(perp.scale(startHeight / 2));
        vertices[1][0] = end.add(perpY.scale(endWidth / 2)).add(perp.scale(endHeight / 2));
        vertices[1][1] = end.add(perpY.scale(endWidth / 2)).subtract(perp.scale(endHeight / 2));
        vertices[1][2] = end.subtract(perpY.scale(endWidth / 2)).subtract(perp.scale(endHeight / 2));
        vertices[1][3] = end.subtract(perpY.scale(endWidth / 2)).add(perp.scale(endHeight / 2));
        return vertices;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    protected boolean isSignificantlyDifferent(Vector3d v1, Vector3d v2) {
        return v1.distanceToSqr(v2) >= 0.00001;
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerWorld) {
            BlockPos projector = getProjectorPos();
            int lifespan = getLifespan();
            if (lifespan > 0) {
                if (tickCount >= lifespan) {
                    remove();
                    return;
                }
            } else if (projector == null) {
                remove();
                return;
            } else {
                TileEntity te = level.getBlockEntity(projector);
                if (!(te instanceof AbstractProjectorTileEntity) || !((AbstractProjectorTileEntity) te).getBeams().contains(getUUID())) {
                    remove();
                    return;
                }
            }
            Vector3d start = position();
            Vector3d original = getEnd();
            Vector3d dir = original.subtract(start).normalize();
            BlockRayTraceResult trace = level.clip(new RayTraceContext(start, start.add(dir.scale(maxRange)), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null));
            Vector3d end = trace.getLocation().add(dir.scale(POKE));
            if (isSignificantlyDifferent(original, end)) {
                setEnd(end, true, true);
                original = end;
            }
            BlockPos endPos = new BlockPos(original);
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
                    int level = entry.getValue();
                    type.onBlockTick(this, pos, level);
                    if (isVisualColliding(pos, state)) {
                        type.onCollisionTick(this, pos, level);
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
    public void remove(boolean keepData) {
        if (!level.isClientSide() && isAlive()) {
            BlockPos endPos = new BlockPos(getEnd());
            BlockState endState = level.getBlockState(endPos);
            Block endBlock = endState.getBlock();
            if (endBlock instanceof IBeamCollisionEffect) {
                ((IBeamCollisionEffect) endBlock).onBeamStopCollision(this, endPos, endState);
            }
            getShape().doBlockEffect(pos -> {
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();
                if (block instanceof IBeamAffectEffect) {
                    ((IBeamAffectEffect) block).onBeamStopAffect(this, pos, state);
                }
            });
        }
        super.remove(keepData);
    }

    protected boolean isVisualColliding(BlockPos pos, BlockState state) {
        Cuboid shape = getShape();
        for (AxisAlignedBB bounds : state.getVisualShape(level, pos, ISelectionContext.empty()).toAabbs()) {
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
            if (level instanceof ServerWorld) {
                Entity parent = ((ServerWorld) level).getEntity(direct);
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

    public Vector3d getEnd() {
        EntityDataManager manager = getEntityData();
        return new Vector3d(manager.get(X), manager.get(Y), manager.get(Z));
    }

    public void setEnd(Vector3d end, boolean start, boolean stop) {
        Vector3d before = getEnd();
        if (!end.equals(before)) {
            setEndRaw(end);
            if (stop) {
                BlockPos beforePos = new BlockPos(before);
                BlockState beforeState = level.getBlockState(beforePos);
                Block beforeBlock = beforeState.getBlock();
                if (beforeBlock instanceof IBeamCollisionEffect) {
                    ((IBeamCollisionEffect) beforeBlock).onBeamStopCollision(this, beforePos, beforeState);
                }
            }
            double length = end.subtract(position()).length();
            double growthRate = getGrowthRate(getModules());
            setEndWidth(getStartWidth() + growthRate * length);
            setEndHeight(getStartHeight() + growthRate * length);
            if (start) {
                BlockPos afterPos = new BlockPos(end);
                BlockState afterState = level.getBlockState(afterPos);
                Block afterBlock = afterState.getBlock();
                if (afterBlock instanceof IBeamCollisionEffect) {
                    ((IBeamCollisionEffect) afterBlock).onBeamStartCollision(this, afterPos, afterState);
                }
            }
        }
    }

    public void setEndRaw(Vector3d end) {
        EntityDataManager manager = getEntityData();
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
            shape = new Cuboid(getVertices(position(), getEnd(), getStartWidth(), getStartHeight(), getEndWidth(), getEndHeight()));
        }
        return shape;
    }

    protected void refreshShape() {
        shape = null;
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
        return getShape().getBounds();
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        if (tag.contains("EndX", Constants.NBT.TAG_DOUBLE) && tag.contains("EndY", Constants.NBT.TAG_DOUBLE) && tag.contains("EndZ", Constants.NBT.TAG_DOUBLE)) {
            setEndRaw(new Vector3d(tag.getDouble("EndX"), tag.getDouble("EndY"), tag.getDouble("EndZ")));
        }
        if (tag.contains("ProjectorX", Constants.NBT.TAG_INT) && tag.contains("ProjectorY", Constants.NBT.TAG_INT) && tag.contains("ProjectorZ", Constants.NBT.TAG_INT)) {
            setProjectorPos(new BlockPos(tag.getInt("ProjectorX"), tag.getInt("ProjectorY"), tag.getInt("ProjectorZ")));
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
        if (tag.contains("Color", Constants.NBT.TAG_INT)) {
            setColor(tag.getInt("Color"));
        }
        if (tag.contains("Layers", Constants.NBT.TAG_INT)) {
            setLayers(tag.getInt("Layers"));
        }
        if (tag.contains("Lifespan", Constants.NBT.TAG_INT)) {
            setLifespan(tag.getInt("Lifespan"));
        }
        if (tag.contains("Parent", Constants.NBT.TAG_INT_ARRAY)) {
            setDirectParent(tag.getUUID("Parent"));
        }
        if (tag.contains("Shooter", Constants.NBT.TAG_INT_ARRAY)) {
            setShooter(tag.getUUID("Shooter"));
        }
        if (tag.contains("MaxRange", Constants.NBT.TAG_DOUBLE)) {
            setMaxRange(tag.getDouble("MaxRange"));
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
        Vector3d end = getEnd();
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
        CompoundNBT modules = new CompoundNBT();
        this.modules.forEach((type, amt) -> modules.putInt(type.getRegistryName().toString(), amt));
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
