package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.te.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Beams.ID);

    public static final RegistryObject<TileEntityType<ContainerProjectorTileEntity>> BEAM_PROJECTOR = register("beam_projector", () -> TileEntityType.Builder.of(ContainerProjectorTileEntity::new, BlockRegistry.PROJECTOR.get(), BlockRegistry.TILTED_PROJECTOR.get()).build(null));
    private TileEntityRegistry() {
    }    public static final RegistryObject<TileEntityType<PointableProjectorTileEntity>> OMNIDIRECTIONAL_BEAM_PROJECTOR = register("omnidirectional_beam_projector", () -> TileEntityType.Builder.of(PointableProjectorTileEntity::new, BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get()).build(null));
    public static final RegistryObject<TileEntityType<RedirectorTileEntity>> MIRROR = register("mirror", () -> TileEntityType.Builder.of(RedirectorTileEntity::new, BlockRegistry.MIRROR.get()).build(null));

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<TileEntityType<T>> type) {
        return TYPES.register(name, type);
    }    public static final RegistryObject<TileEntityType<PointableRedirectorTileEntity>> OMNIDIRECTIONAL_MIRROR = register("omnidirectional_mirror", () -> TileEntityType.Builder.of(PointableRedirectorTileEntity::new, BlockRegistry.OMNIDIRECTIONAL_MIRROR.get()).build(null));

    public static final RegistryObject<TileEntityType<BeamSensorTileEntity>> BEAM_SENSOR = register("beam_sensor", () -> TileEntityType.Builder.of(BeamSensorTileEntity::new, BlockRegistry.PHOTODETECTOR.get()).build(null));
    public static final RegistryObject<TileEntityType<TurretTileEntity>> TURRET = register("turret", () -> TileEntityType.Builder.of(TurretTileEntity::new, BlockRegistry.TURRET.get()).build(null));





}
