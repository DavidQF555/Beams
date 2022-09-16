package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.util.FastColor;

public class ColorModuleType extends ProjectorModuleType {

    private final int color;

    public ColorModuleType(int color) {
        this.color = color;
    }

    private static int color(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static int merge(int color1, int color2) {
        return color(FastColor.ARGB32.alpha(color1) * FastColor.ARGB32.alpha(color2) / 255, FastColor.ARGB32.red(color1) * FastColor.ARGB32.red(color2) / 255, FastColor.ARGB32.green(color1) * FastColor.ARGB32.green(color2) / 255, FastColor.ARGB32.blue(color1) * FastColor.ARGB32.blue(color2) / 255);
    }

    public int getColor() {
        return color;
    }

    @Override
    public void onStart(BeamEntity beam, int amt) {
        int merged = beam.getColor();
        int color = getColor();
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        int opaque = color(255, red, green, blue);
        for (int i = 0; i < amt; i++) {
            merged = merge(beam.getColor(), opaque);
        }
        beam.setColor(merged);
    }

}