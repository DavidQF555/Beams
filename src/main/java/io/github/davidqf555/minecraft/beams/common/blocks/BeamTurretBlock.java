package io.github.davidqf555.minecraft.beams.common.blocks;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class BeamTurretBlock extends OmnidirectionalProjectorBlock {

    public static final BooleanProperty IN_RANGE = BlockStateProperties.ENABLED;
    public static final MapCodec<BeamTurretBlock> CODEC = simpleCodec(BeamTurretBlock::new);

    public BeamTurretBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false).setValue(IN_RANGE, false));
    }

    @Override
    protected MapCodec<? extends BeamTurretBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityRegistry.TURRET.get(), AbstractProjectorTileEntity::tick);
    }

    @Override
    public TurretTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TurretTileEntity(pos, state);
    }

    @Override
    public boolean isActive(BlockState state) {
        return super.isActive(state) && state.getValue(IN_RANGE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IN_RANGE);
    }

}
