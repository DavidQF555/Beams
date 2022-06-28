package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.util.ColorHelper;

public class ColorModuleType extends ProjectorModuleType {

    private final int color;

    public ColorModuleType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void onStart(BeamEntity beam) {
        int alpha = ColorHelper.PackedColor.alpha(beam.getColor());
        int color = getColor();
        int red = ColorHelper.PackedColor.red(color);
        int green = ColorHelper.PackedColor.green(color);
        int blue = ColorHelper.PackedColor.blue(color);
        beam.setColor(ColorHelper.PackedColor.color(alpha, red, green, blue));
    }

}