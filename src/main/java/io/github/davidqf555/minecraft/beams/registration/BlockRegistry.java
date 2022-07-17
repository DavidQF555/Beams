package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.OmnidirectionalProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.blocks.ProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.blocks.TiltedProjectorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Beams.ID);

    public static final RegistryObject<ProjectorBlock> PROJECTOR = register("projector", () -> new ProjectorBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<TiltedProjectorBlock> TILTED_PROJECTOR = register("tilted_projector", () -> new TiltedProjectorBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<OmnidirectionalProjectorBlock> OMNIDIRECTIONAL_PROJECTOR = register("omnidirectional_projector", () -> new OmnidirectionalProjectorBlock(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

}
