package io.github.davidqf555.minecraft.beams.common.entities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class DoubleSerializer implements EntityDataSerializer<Double> {

    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    protected DoubleSerializer() {
    }

    @Override
    public void write(FriendlyByteBuf packet, Double value) {
        packet.writeDouble(value);
    }

    @Override
    public Double read(FriendlyByteBuf packet) {
        return packet.readDouble();
    }

    @Override
    public Double copy(Double value) {
        return value;
    }
}
