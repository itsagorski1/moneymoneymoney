package com.jonah.code.java.mods.moneymoneymoney;

import com.jonah.code.java.mods.moneymoneymoney.network.ModMessages;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModBlocks;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModEntityTypes;
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

        ModBlocks.BLOCKS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }
}
