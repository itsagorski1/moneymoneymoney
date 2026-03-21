package com.jonah.code.java.mods.moneymoneymoney.registry;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.item.MoneyItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MoneyMoneyMoneyMod.MOD_ID);

    public static final RegistryObject<Item> MONEY =
            ITEMS.register("money", () -> new MoneyItem(new Item.Properties()));

    private ModItems() {
    }
}
