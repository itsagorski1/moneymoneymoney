package com.jonah.code.java.mods.moneymoneymoney.registry;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCreativeModeTabEvents {
    private ModCreativeModeTabEvents() {
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ONE_DOLLAR);
            event.accept(ModItems.FIVE_DOLLARS);
            event.accept(ModItems.TEN_DOLLARS);
            event.accept(ModItems.TWENTY_DOLLARS);
            event.accept(ModItems.FIFTY_DOLLARS);
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.BANKER_SPAWN_EGG);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.BANK_TILES);
            event.accept(ModItems.VAULT_PLATING);
            event.accept(ModItems.TELLER_GLASS);
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.BANK_COMPUTER);
            event.accept(ModItems.BANK_SEAT);
        }
    }
}
