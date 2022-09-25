package io.github.davidqf555.minecraft.beams.common.entities;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public class DoubleSerializer implements IDataSerializer<Double> {

    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    protected DoubleSerializer() {
    }

    @Override
    public void write(PacketBuffer packet, Double value) {
        packet.writeDouble(value);
    }

    @Override
    public Double read(PacketBuffer packet) {
        return packet.readDouble();
    }

    @Override
    public Double copy(Double value) {
        return value;
    }
}
