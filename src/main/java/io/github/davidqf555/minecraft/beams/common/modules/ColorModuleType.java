package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.util.ColorHelper;

public class ColorModuleType extends ProjectorModuleType {

    private final int color;

    public ColorModuleType(int color) {
        this.color = color;
    }

    private static int color(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static int merge(int color1, int color2) {
        return color(alpha(color1) * alpha(color2) / 255, ColorHelper.PackedColor.red(color1) * ColorHelper.PackedColor.red(color2) / 255, ColorHelper.PackedColor.green(color1) * ColorHelper.PackedColor.green(color2) / 255, ColorHelper.PackedColor.blue(color1) * ColorHelper.PackedColor.blue(color2) / 255);
    }

    private static int alpha(int color) {
        return color >>> 24;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void onStart(BeamEntity beam, int amt) {
        int merged = beam.getColor();
        int color = getColor();
        int red = ColorHelper.PackedColor.red(color);
        int green = ColorHelper.PackedColor.green(color);
        int blue = ColorHelper.PackedColor.blue(color);
        int opaque = color(255, red, green, blue);
        for (int i = 0; i < amt; i++) {
            merged = merge(beam.getColor(), opaque);
        }
        beam.setColor(merged);
    }

}