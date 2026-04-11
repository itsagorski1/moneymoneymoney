package com.jonah.code.java.mods.moneymoneymoney.registry;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.item.MoneyItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MoneyMoneyMoneyMod.MOD_ID);

    public static final RegistryObject<Item> ONE_DOLLAR =
            ITEMS.register("one_dollar", () -> new MoneyItem(new Item.Properties(), 1));
    public static final RegistryObject<Item> FIVE_DOLLARS =
            ITEMS.register("five_dollars", () -> new MoneyItem(new Item.Properties(), 5));
    public static final RegistryObject<Item> TEN_DOLLARS =
            ITEMS.register("ten_dollars", () -> new MoneyItem(new Item.Properties(), 10));
    public static final RegistryObject<Item> TWENTY_DOLLARS =
            ITEMS.register("twenty_dollars", () -> new MoneyItem(new Item.Properties(), 20));
    public static final RegistryObject<Item> FIFTY_DOLLARS =
            ITEMS.register("fifty_dollars", () -> new MoneyItem(new Item.Properties(), 50));
    public static final RegistryObject<Item> BANKER_SPAWN_EGG =
            ITEMS.register("banker_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntityTypes.BANKER, 0x1F2A38, 0xE6E9EF, new Item.Properties()));
    public static final RegistryObject<Item> BANK_TILES = registerBlockItem("bank_tiles", ModBlocks.BANK_TILES);
    public static final RegistryObject<Item> VAULT_PLATING = registerBlockItem("vault_plating", ModBlocks.VAULT_PLATING);
    public static final RegistryObject<Item> TELLER_GLASS = registerBlockItem("teller_glass", ModBlocks.TELLER_GLASS);
    public static final RegistryObject<Item> BANK_COMPUTER = registerBlockItem("bank_computer", ModBlocks.BANK_COMPUTER);
    public static final RegistryObject<Item> BANK_SEAT = registerBlockItem("bank_seat", ModBlocks.BANK_SEAT);

    private ModItems() {
    }

    private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
