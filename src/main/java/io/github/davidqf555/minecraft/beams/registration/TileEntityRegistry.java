package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.DirectionalProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.ProjectorTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Beams.ID);

    private TileEntityRegistry() {
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<BlockEntityType<ProjectorTileEntity>> BEAM_PROJECTOR = register("beam_projector", () -> BlockEntityType.Builder.of(ProjectorTileEntity::new, BlockRegistry.PROJECTOR.get(), BlockRegistry.TILTED_PROJECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<DirectionalProjectorTileEntity>> OMNIDIRECTIONAL_BEAM_PROJECTOR = register("omnidirectional_beam_projector", () -> BlockEntityType.Builder.of(DirectionalProjectorTileEntity::new, BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get()).build(null));

}
