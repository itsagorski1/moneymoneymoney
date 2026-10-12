package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public final class PlayerMoneyHelper {
    private PlayerMoneyHelper() {
    }

    public static Optional<IPlayerMoneyData> getMoneyData(Player player) {
        return player.getCapability(ModCapabilities.PLAYER_MONEY).resolve();
    }
}
