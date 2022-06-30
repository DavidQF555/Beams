package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.util.FastColor;

public class ColorModuleType extends ProjectorModuleType {

    private final int color;

    public ColorModuleType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void onStart(BeamEntity beam, int amt) {
        int alpha = FastColor.ARGB32.alpha(beam.getColor());
        int color = getColor();
        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        beam.setColor(FastColor.ARGB32.color(alpha, red, green, blue));
    }

}