package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.BeamSensorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class BeamSensorBlock extends BaseEntityBlock implements IBeamCollisionEffect {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public BeamSensorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        BlockEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof BeamSensorTileEntity && ((BeamSensorTileEntity) te).addHit(beam.getUUID())) {
            beam.level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
            te.setChanged();
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        BlockEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof BeamSensorTileEntity && ((BeamSensorTileEntity) te).removeHit(beam.getUUID())) {
            if (((BeamSensorTileEntity) te).getHit().isEmpty()) {
                beam.level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, false));
            }
            te.setChanged();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeamSensorTileEntity(pos, state);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(TRIGGERED);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter reader, BlockPos pos, Direction direction) {
        return state.getValue(TRIGGERED) ? 15 : 0;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
