package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.MirrorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Beams.ID);

    private TileEntityRegistry() {
    }

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<TileEntityType<T>> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<TileEntityType<ContainerProjectorTileEntity>> BEAM_PROJECTOR = register("beam_projector", () -> TileEntityType.Builder.of(ContainerProjectorTileEntity::new, BlockRegistry.PROJECTOR.get(), BlockRegistry.TILTED_PROJECTOR.get()).build(null));
    public static final RegistryObject<TileEntityType<OmnidirectionalProjectorTileEntity>> OMNIDIRECTIONAL_BEAM_PROJECTOR = register("omnidirectional_beam_projector", () -> TileEntityType.Builder.of(OmnidirectionalProjectorTileEntity::new, BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get()).build(null));
    public static final RegistryObject<TileEntityType<MirrorTileEntity>> MIRROR = register("mirror", () -> TileEntityType.Builder.of(MirrorTileEntity::new, BlockRegistry.MIRROR.get()).build(null));

}
