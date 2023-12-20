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
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
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
    private static final double SEGMENT_LENGTH = 4;
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
    private final Map<BlockPos, BlockState> affecting = new HashMap<>();
    private BlockPos projector;
    private double maxRange;
    private UUID shooter, parent;
    private int lifespan;
    private AxisAlignedBB maxBounds;
    private boolean updateAffectingPositions = true;

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

    private static Vector3d[] getVertices(Vector3d start, Vector3d end, double startWidth, double startHeight, double endWidth, double endHeight) {
        Vector3d[] vertices = new Vector3d[8];
        Vector3d center = end.subtract(start);
        Vector3d perpY = center.cross(new Vector3d(Vector3f.YP)).normalize();
        if (perpY.lengthSqr() == 0) {
            perpY = new Vector3d(Vector3f.ZP);
        }
        Vector3d perp = center.cross(perpY).normalize();
        vertices[0] = start.add(perpY.scale(startWidth / 2)).add(perp.scale(startHeight / 2));
        vertices[1] = start.add(perpY.scale(startWidth / 2)).subtract(perp.scale(startHeight / 2));
        vertices[2] = start.subtract(perpY.scale(startWidth / 2)).add(perp.scale(startHeight / 2));
        vertices[3] = start.subtract(perpY.scale(startWidth / 2)).subtract(perp.scale(startHeight / 2));
        vertices[4] = end.add(perpY.scale(endWidth / 2)).add(perp.scale(endHeight / 2));
        vertices[5] = end.add(perpY.scale(endWidth / 2)).subtract(perp.scale(endHeight / 2));
        vertices[6] = end.subtract(perpY.scale(endWidth / 2)).add(perp.scale(endHeight / 2));
        vertices[7] = end.subtract(perpY.scale(endWidth / 2)).subtract(perp.scale(endHeight / 2));
        return vertices;
    }

    private static AxisAlignedBB getMaxBounds(Vector3d start, Vector3d end, double startWidth, double startHeight, double endWidth, double endHeight) {
        Vector3d[] vertices = getVertices(start, end, startWidth, startHeight, endWidth, endHeight);
        double minX = Arrays.stream(vertices).mapToDouble(Vector3d::x).min().getAsDouble();
        double maxX = Arrays.stream(vertices).mapToDouble(Vector3d::x).max().getAsDouble();
        double minY = Arrays.stream(vertices).mapToDouble(Vector3d::y).min().getAsDouble();
        double maxY = Arrays.stream(vertices).mapToDouble(Vector3d::y).max().getAsDouble();
        double minZ = Arrays.stream(vertices).mapToDouble(Vector3d::z).min().getAsDouble();
        double maxZ = Arrays.stream(vertices).mapToDouble(Vector3d::z).max().getAsDouble();
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
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
            if (updateAffectingPositions) {
                Set<BlockPos> past = affecting.keySet();
                Set<BlockPos> current = getAffectingPositions();
                past.forEach(pos -> {
                    if (!current.contains(pos)) {
                        BlockState state = affecting.get(pos);
                        if (state.getBlock() instanceof IBeamAffectEffect) {
                            ((IBeamAffectEffect) state.getBlock()).onBeamStopAffect(this, pos, state);
                        }
                    }
                });
                current.forEach(pos -> {
                    if (!past.contains(pos)) {
                        affecting.put(pos, null);
                    }
                });
                updateAffectingPositions = false;
            }
            for (BlockPos pos : affecting.keySet()) {
                BlockState past = affecting.get(pos);
                BlockState state = level.getBlockState(pos);
                if (!state.equals(past)) {
                    if (past != null && past.getBlock() instanceof IBeamAffectEffect) {
                        ((IBeamAffectEffect) past.getBlock()).onBeamStopAffect(this, pos, past);
                    }
                    if (state.getBlock() instanceof IBeamAffectEffect) {
                        ((IBeamAffectEffect) state.getBlock()).onBeamStartAffect(this, pos, state);
                    }
                    affecting.put(pos, state);
                }
            }
            Map<ProjectorModuleType, Integer> modules = getModules();
            Set<Map.Entry<ProjectorModuleType, Integer>> blockModules = modules.entrySet().stream().filter(entry -> entry.getValue() > 0 && entry.getKey().shouldTickBlocks()).collect(Collectors.toSet());
            if (!blockModules.isEmpty()) {
                affecting.forEach((pos, state) -> {
                    modules.forEach((type, amt) -> type.onBlockTick(this, pos, amt));
                    if (isVisualColliding(pos, state)) {
                        modules.forEach((type, amt) -> type.onCollisionTick(this, pos, amt));
                    }
                });
            }
            Set<Map.Entry<ProjectorModuleType, Integer>> entities = modules.entrySet().stream().filter(entry -> entry.getValue() > 0 && entry.getKey().shouldTickEntities()).collect(Collectors.toSet());
            if (!entities.isEmpty()) {
                for (Entity entity : level.getEntities(this, getMaxBounds())) {
                    if (isAffected(entity)) {
                        entities.forEach(entry -> {
                            entry.getKey().onEntityTick(this, entity, entry.getValue());
                        });
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
            affecting.forEach((pos, state) -> {
                Block block = state.getBlock();
                if (block instanceof IBeamAffectEffect) {
                    ((IBeamAffectEffect) block).onBeamStopAffect(this, pos, state);
                }
            });
        }
        super.remove(keepData);
    }

    protected boolean isVisualColliding(BlockPos pos, BlockState state) {
        for (AxisAlignedBB bounds : state.getVisualShape(level, pos, ISelectionContext.empty()).toAabbs()) {
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
        Vector3d start = position();
        Vector3d center = getEnd().subtract(start);
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
        refreshBounds();
    }

    public double getStartWidth() {
        return getEntityData().get(START_WIDTH);
    }

    public void setStartWidth(double width) {
        getEntityData().set(START_WIDTH, width);
        refreshBounds();
    }

    public double getStartHeight() {
        return getEntityData().get(START_HEIGHT);
    }

    public void setStartHeight(double height) {
        getEntityData().set(START_HEIGHT, height);
        refreshBounds();
    }

    public double getEndWidth() {
        return getEntityData().get(END_WIDTH);
    }

    public void setEndWidth(double width) {
        getEntityData().set(END_WIDTH, width);
        refreshBounds();
    }

    public double getEndHeight() {
        return getEntityData().get(END_HEIGHT);
    }

    public void setEndHeight(double height) {
        getEntityData().set(END_HEIGHT, height);
        refreshBounds();
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        refreshBounds();
    }

    private Set<BlockPos> getAffectingPositions() {
        Vector3d start = position();
        Vector3d dir = getEnd().subtract(position());
        double length = dir.length();
        dir = dir.scale(1 / length);
        Set<BlockPos> affectingPos = new HashSet<>();
        double baseStartWidth = getStartWidth();
        double baseStartHeight = getStartHeight();
        double baseEndWidth = getEndWidth();
        double baseEndHeight = getEndHeight();
        int count = MathHelper.ceil(length / SEGMENT_LENGTH);
        for (int i = 0; i < count; i++) {
            double startPos = SEGMENT_LENGTH * i;
            double endPos = Math.min(length, SEGMENT_LENGTH * (i + 1));
            Vector3d s = start.add(dir.scale(startPos));
            Vector3d e = start.add(dir.scale(endPos));
            double startWidth = baseStartWidth + (baseEndWidth - baseStartWidth) * startPos / length;
            double startHeight = baseStartHeight + (baseEndHeight - baseStartHeight) * startPos / length;
            double endWidth = baseStartWidth + (baseEndWidth - baseStartWidth) * endPos / length;
            double endHeight = baseStartHeight + (baseEndHeight - baseStartHeight) * endPos / length;
            AxisAlignedBB bounds = getMaxBounds(s, e, startWidth, startHeight, endWidth, endHeight);
            for (int x = MathHelper.floor(bounds.minX); x <= MathHelper.floor(bounds.maxX); x++) {
                for (int z = MathHelper.floor(bounds.minZ); z <= MathHelper.floor(bounds.maxZ); z++) {
                    for (int y = MathHelper.floor(bounds.minY); y <= MathHelper.floor(bounds.maxY); y++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (isAffected(pos)) {
                            affectingPos.add(pos);
                        }
                    }
                }
            }
        }
        return affectingPos;
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

    public AxisAlignedBB getMaxBounds() {
        if (maxBounds == null) {
            maxBounds = getMaxBounds(position(), getEnd(), getStartWidth(), getStartHeight(), getEndWidth(), getEndHeight());
        }
        return maxBounds;
    }

    protected void refreshBounds() {
        updateAffectingPositions = true;
        maxBounds = null;
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
        if (tag.contains("UpdateAffecting", Constants.NBT.TAG_BYTE)) {
            updateAffectingPositions = tag.getBoolean("UpdateAffecting");
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
        if (tag.contains("Affecting", Constants.NBT.TAG_LIST)) {
            for (INBT nbt : tag.getList("Affecting", Constants.NBT.TAG_COMPOUND)) {
                if (((CompoundNBT) nbt).contains("Pos", Constants.NBT.TAG_COMPOUND) && ((CompoundNBT) nbt).contains("State", Constants.NBT.TAG_COMPOUND)) {
                    affecting.put(NBTUtil.readBlockPos(((CompoundNBT) nbt).getCompound("Pos")), NBTUtil.readBlockState(((CompoundNBT) nbt).getCompound("State")));
                }
            }
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
        tag.putBoolean("UpdateAffecting", updateAffectingPositions);
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
        ListNBT collisions = new ListNBT();
        affecting.forEach((pos, state) -> {
            CompoundNBT collision = new CompoundNBT();
            collision.put("Pos", NBTUtil.writeBlockPos(pos));
            collision.put("State", NBTUtil.writeBlockState(state));
            collisions.add(collision);
        });
        tag.put("Affecting", collisions);
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
