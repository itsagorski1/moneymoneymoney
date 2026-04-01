package com.jonah.code.java.mods.moneymoneymoney.entity;

import com.jonah.code.java.mods.moneymoneymoney.menu.MoneyMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BankerEntity extends Villager {
    public BankerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
        setCustomName(Component.translatable("entity.moneymoneymoney.banker"));
        setCustomNameVisible(true);
        setPersistenceRequired();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            MoneyMenu.open(serverPlayer);
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }
}
