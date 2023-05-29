package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.world.IBlockReader;

public class BeamTurretBlock extends OmnidirectionalProjectorBlock {

    public static final BooleanProperty IN_RANGE = BlockStateProperties.ENABLED;

    public BeamTurretBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false).setValue(IN_RANGE, false));
    }

    @Override
    public TurretTileEntity newBlockEntity(IBlockReader reader) {
        return new TurretTileEntity();
    }

    @Override
    public boolean isActive(BlockState state) {
        return super.isActive(state) && state.getValue(IN_RANGE);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IN_RANGE);
    }

}
