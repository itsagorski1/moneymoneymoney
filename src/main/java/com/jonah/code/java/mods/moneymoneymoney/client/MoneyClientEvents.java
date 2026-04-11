package com.jonah.code.java.mods.moneymoneymoney.client;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.client.render.BankerRenderer;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModEntityTypes;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModMenus;
<<<<<<< HEAD
=======
import net.minecraft.client.renderer.entity.EntityRenderers;
>>>>>>> 6d24ade36e9df9b876f1607df8e76ac0d8a21cb0
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
<<<<<<< HEAD
        event.enqueueWork(() -> MenuScreens.register(ModMenus.MONEY_MENU.get(), MoneyScreen::new));
=======
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.MONEY_MENU.get(), MoneyScreen::new);
            EntityRenderers.register(ModEntityTypes.BANKER.get(), BankerRenderer::new);
        });
>>>>>>> 6d24ade36e9df9b876f1607df8e76ac0d8a21cb0
    }
}
