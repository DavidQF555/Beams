package io.github.davidqf555.minecraft.beams.common.modules.targeting;

import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface TargetingModuleType {

    @Nullable
    Vector3d tick(TurretTileEntity te, double range);

}
