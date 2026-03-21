package com.jonah.code.java.mods.moneymoneymoney;

import com.jonah.code.java.mods.moneymoneymoney.network.ModMessages;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModItems;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MoneyMoneyMoneyMod.MOD_ID)
public class MoneyMoneyMoneyMod {
    public static final String MOD_ID = "moneymoneymoney";

    public MoneyMoneyMoneyMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }
}
