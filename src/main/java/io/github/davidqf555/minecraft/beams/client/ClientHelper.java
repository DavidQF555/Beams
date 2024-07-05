package io.github.davidqf555.minecraft.beams.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.UUID;

public final class ClientHelper {

    private ClientHelper() {
    }

    @Nullable
    public static Component getDisplayName(UUID player) {
        ClientPacketListener client = Minecraft.getInstance().getConnection();
        if (client != null) {
            PlayerInfo info = client.getPlayerInfo(player);
            if (info != null) {
                return info.getTabListDisplayName();
            }
        }
        return null;
    }

}
