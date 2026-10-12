package com.jonah.code.java.mods.moneymoneymoney.entity;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID)
public final class VillagerCurrencyEvents {
    private VillagerCurrencyEvents() {
    }

    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide || !(event.getTarget() instanceof Villager villager)
                || villager.isBaby() || villager.isTrading()) {
            return;
        }

        // Generate offers if needed, then migrate saved and newly unlocked trades
        // before vanilla applies player discounts and sends the trading screen.
        MerchantOffers offers = villager.getOffers();
        for (int i = 0; i < offers.size(); i++) {
            MerchantOffer offer = offers.get(i);
            if (!offer.getBaseCostA().is(Items.EMERALD)
                    && !offer.getCostB().is(Items.EMERALD)
                    && !offer.getResult().is(Items.EMERALD)) {
                continue;
            }

            // Round-trip the complete offer to retain uses, demand, discounts,
            // experience rewards and the original counts and item metadata.
            CompoundTag tag = offer.createTag();
            replaceEmerald(tag.getCompound("buy"));
            replaceEmerald(tag.getCompound("buyB"));
            replaceEmerald(tag.getCompound("sell"));
            offers.set(i, new MerchantOffer(tag));
        }
    }

    private static void replaceEmerald(CompoundTag stack) {
        if ("minecraft:emerald".equals(stack.getString("id"))) {
            stack.putString("id", ForgeRegistries.ITEMS.getKey(ModItems.ONE_DOLLAR.get()).toString());
        }
    }
}
