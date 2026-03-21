package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PlayerMoneyCapabilityRegistration {
    private PlayerMoneyCapabilityRegistration() {
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPlayerMoneyData.class);
    }
}
