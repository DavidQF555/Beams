package io.github.davidqf555.minecraft.beams.common.modules.targeting;

import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface TargetingModuleType {

    @Nullable
    Vec3 tick(TurretTileEntity te, double range);

}
