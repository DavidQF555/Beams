package io.github.davidqf555.minecraft.beams.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public final class ClientHelper {

    private ClientHelper() {
    }

    public static Component getDisplayName(UUID id) {
        ClientPacketListener client = Minecraft.getInstance().getConnection();
        if (client != null) {
            PlayerInfo info = client.getPlayerInfo(id);
            if (info != null) {
                Component name = info.getTabListDisplayName();
                if (name != null) {
                    return name;
                }
            }
        }
        return Component.empty();
    }

}
