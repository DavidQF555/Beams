package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.te.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Beams.ID);

    private TileEntityRegistry() {
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<BlockEntityType<ContainerProjectorTileEntity>> BEAM_PROJECTOR = register("beam_projector", () -> BlockEntityType.Builder.of(ContainerProjectorTileEntity::new, BlockRegistry.PROJECTOR.get(), BlockRegistry.TILTED_PROJECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<OmnidirectionalProjectorTileEntity>> OMNIDIRECTIONAL_BEAM_PROJECTOR = register("omnidirectional_beam_projector", () -> BlockEntityType.Builder.of(OmnidirectionalProjectorTileEntity::new, BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<RedirectorTileEntity>> MIRROR = register("mirror", () -> BlockEntityType.Builder.of(RedirectorTileEntity::new, BlockRegistry.MIRROR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BeamSensorTileEntity>> BEAM_SENSOR = register("beam_sensor", () -> BlockEntityType.Builder.of(BeamSensorTileEntity::new, BlockRegistry.PHOTODETECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<OmnidirectionalMirrorTileEntity>> OMNIDIRECTIONAL_MIRROR = register("omnidirectional_mirror", () -> BlockEntityType.Builder.of(OmnidirectionalMirrorTileEntity::new, BlockRegistry.OMNIDIRECTIONAL_MIRROR.get()).build(null));

}
