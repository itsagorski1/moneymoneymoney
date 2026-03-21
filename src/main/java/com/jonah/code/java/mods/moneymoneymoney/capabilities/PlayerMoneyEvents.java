package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID)
public final class PlayerMoneyEvents {
    private static final ResourceLocation PLAYER_MONEY_ID = new ResourceLocation(MoneyMoneyMoneyMod.MOD_ID, "player_money");

    private PlayerMoneyEvents() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(PLAYER_MONEY_ID, new PlayerMoneyProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        PlayerMoneyHelper.getMoneyData(event.getOriginal()).ifPresent(oldData ->
                PlayerMoneyHelper.getMoneyData(event.getEntity()).ifPresent(newData -> {
                    newData.setWalletBalance(oldData.getWalletBalance());
                    newData.setBankBalance(oldData.getBankBalance());
                }));
        event.getOriginal().invalidateCaps();
    }
}
