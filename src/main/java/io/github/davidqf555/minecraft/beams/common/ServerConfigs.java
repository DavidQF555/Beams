package io.github.davidqf555.minecraft.beams.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs {

    public static final ServerConfigs INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ServerConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.DoubleValue beamSegmentLength, projectorMaxRange, defaultBeamSize;
    public final ForgeConfigSpec.IntValue projectorUpdatePeriod, portableProjectorMaxRange;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Beams mod");
        beamSegmentLength = builder.comment("Beams are separated into segments with a max of this length in blocks in order to improve efficiency and ensure consistent rendering. ")
                .defineInRange("segmentLength", 16, 0.5, Double.MAX_VALUE);
        defaultBeamSize = builder.comment("This is the the default width/height in blocks of beams projected from projectors. ")
                .defineInRange("beamSize", 0.5, 0, Double.MAX_VALUE);
        projectorMaxRange = builder.comment("This is the max range in blocks that a projector projects beams. ")
                .defineInRange("projectorRange", 64, 0, Double.MAX_VALUE);
        portableProjectorMaxRange = builder.comment("This is the max range in blocks that a portable projector projects beams. ")
                .defineInRange("portableProjectorRange", 64, 0, Integer.MAX_VALUE);
        projectorUpdatePeriod = builder.comment("This is the period in ticks that projectors update their beams. Lowering this would cause more consistent beams but worse performance. ")
                .defineInRange("projectorUpdatePeriod", 20, 1, Integer.MAX_VALUE);
        builder.pop();
    }

}
