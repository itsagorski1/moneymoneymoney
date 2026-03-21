package com.jonah.code.java.mods.moneymoneymoney.client;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModMenus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MoneyClientEvents {
    private MoneyClientEvents() {
    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MONEY_MENU.get(), MoneyScreen::new);
    }
}
