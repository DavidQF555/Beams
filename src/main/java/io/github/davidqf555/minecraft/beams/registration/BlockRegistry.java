package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.*;
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

    public static final RegistryObject<SimpleProjectorBlock> PROJECTOR = register("projector", () -> new SimpleProjectorBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<TiltedProjectorBlock> TILTED_PROJECTOR = register("tilted_projector", () -> new TiltedProjectorBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<OmnidirectionalProjectorBlock> OMNIDIRECTIONAL_PROJECTOR = register("omnidirectional_projector", () -> new PointableProjectorBlock(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));
    public static final RegistryObject<SimpleMirrorBlock> MIRROR = register("mirror", () -> new SimpleMirrorBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<BeamSensorBlock> PHOTODETECTOR = register("photodetector", () -> new BeamSensorBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final RegistryObject<OmnidirectionalMirrorBlock> OMNIDIRECTIONAL_MIRROR = register("omnidirectional_mirror", () -> new OmnidirectionalMirrorBlock(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));
    public static final RegistryObject<BeamTurretBlock> TURRET = register("turret", () -> new BeamTurretBlock(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

}
