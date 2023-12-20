package io.github.davidqf555.minecraft.beams.common;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs {

    public static final ServerConfigs INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<ServerConfigs, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.DoubleValue projectorMaxRange, defaultBeamSize, pointerRange;
    public final ModConfigSpec.IntValue portableProjectorMaxRange;

    public ServerConfigs(ModConfigSpec.Builder builder) {
        builder.push("Server config for Beams mod");
        defaultBeamSize = builder.comment("This is the the default width/height in blocks of beams projected from projectors. ")
                .defineInRange("beamSize", 0.5, 0, Double.MAX_VALUE);
        projectorMaxRange = builder.comment("This is the max range in blocks that a projector projects beams. ")
                .defineInRange("projectorRange", 64, 0, Double.MAX_VALUE);
        portableProjectorMaxRange = builder.comment("This is the max range in blocks that a portable projector projects beams. ")
                .defineInRange("portableProjectorRange", 64, 0, Integer.MAX_VALUE);
        pointerRange = builder.comment("This is the range in blocks of the projector pointer")
                .defineInRange("pointerRange", 64, 0, Double.MAX_VALUE);
        builder.pop();
    }

}
