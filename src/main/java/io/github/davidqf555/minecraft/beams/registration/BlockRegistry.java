package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Beams.ID);

    public static final DeferredHolder<Block, SimpleProjectorBlock> PROJECTOR = register("projector", () -> new SimpleProjectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f)));
    public static final DeferredHolder<Block, TiltedProjectorBlock> TILTED_PROJECTOR = register("tilted_projector", () -> new TiltedProjectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f)));
    public static final DeferredHolder<Block, OmnidirectionalProjectorBlock> OMNIDIRECTIONAL_PROJECTOR = register("omnidirectional_projector", () -> new PointableProjectorBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));
    public static final DeferredHolder<Block, SimpleMirrorBlock> MIRROR = register("mirror", () -> new SimpleMirrorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(3.5f)));
    public static final DeferredHolder<Block, BeamSensorBlock> PHOTODETECTOR = register("photodetector", () -> new BeamSensorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f)));
    public static final DeferredHolder<Block, OmnidirectionalMirrorBlock> OMNIDIRECTIONAL_MIRROR = register("omnidirectional_mirror", () -> new OmnidirectionalMirrorBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));
    public static final DeferredHolder<Block, BeamTurretBlock> TURRET = register("turret", () -> new BeamTurretBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).noOcclusion().isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false).strength(0.3f)));

    private BlockRegistry() {
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

}
