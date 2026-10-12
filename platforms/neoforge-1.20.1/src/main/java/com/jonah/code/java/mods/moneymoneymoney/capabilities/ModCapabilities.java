package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class ModCapabilities {
    public static final Capability<IPlayerMoneyData> PLAYER_MONEY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private ModCapabilities() {
    }
}
