package com.jonah.code.java.mods.moneymoneymoney.client;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.client.render.BankerRenderer;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModEntityTypes;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModMenus;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MoneyClientEvents {
    private MoneyClientEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.MONEY_MENU.get(), MoneyScreen::new);
            EntityRenderers.register(ModEntityTypes.BANKER.get(), BankerRenderer::new);
        });
    }
}
