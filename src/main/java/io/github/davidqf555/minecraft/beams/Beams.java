package io.github.davidqf555.minecraft.beams;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("beams")
public class Beams {

    public static final String ID = "beams";

    public Beams() {
        MinecraftForge.EVENT_BUS.register(this);
    }

}
