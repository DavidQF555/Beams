package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.ProjectorTileEntity;
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

    public static final RegistryObject<TileEntityType<ProjectorTileEntity>> BEAM_PROJECTOR = register("beam_projector", () -> TileEntityType.Builder.of(ProjectorTileEntity::new, BlockRegistry.PROJECTOR.get()).build(null));


}
