package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;

import java.util.function.Function;

public class LayersModuleType extends ProjectorModuleType {

    private final Function<Integer, Integer> layers;

    public LayersModuleType(Function<Integer, Integer> layers) {
        this.layers = layers;
    }

    @Override
    public void onStart(BeamEntity beam, int amt) {
        beam.setLayers(beam.getLayers() + layers.apply(amt));
    }
}
