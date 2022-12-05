package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.MirrorBlock;
import io.github.davidqf555.minecraft.beams.common.blocks.OmnidirectionalProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.blocks.SimpleProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.blocks.TiltedProjectorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Beams.ID);

    public static final RegistryObject<SimpleProjectorBlock> PROJECTOR = register("projector", () -> new SimpleProjectorBlock(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<TiltedProjectorBlock> TILTED_PROJECTOR = register("tilted_projector", () -> new TiltedProjectorBlock(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<OmnidirectionalProjectorBlock> OMNIDIRECTIONAL_PROJECTOR = register("omnidirectional_projector", () -> new OmnidirectionalProjectorBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));
    public static final RegistryObject<MirrorBlock> MIRROR = register("mirror", () -> new MirrorBlock(AbstractBlock.Properties.of(Material.METAL).strength(3.5f)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

}
