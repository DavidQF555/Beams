package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.BeamSensorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BeamSensorBlock extends ContainerBlock implements IBeamCollisionEffect {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public BeamSensorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof BeamSensorTileEntity && ((BeamSensorTileEntity) te).addHit(beam.getUUID())) {
            beam.level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
            te.setChanged();
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof BeamSensorTileEntity && ((BeamSensorTileEntity) te).removeHit(beam.getUUID())) {
            if (((BeamSensorTileEntity) te).getHit().isEmpty()) {
                beam.level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, false));
            }
            te.setChanged();
        }
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new BeamSensorTileEntity();
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(TRIGGERED);
    }

    @Override
    public int getSignal(BlockState state, IBlockReader reader, BlockPos pos, Direction direction) {
        return state.getValue(TRIGGERED) ? 15 : 0;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
