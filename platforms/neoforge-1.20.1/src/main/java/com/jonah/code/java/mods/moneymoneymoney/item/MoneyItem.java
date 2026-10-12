package com.jonah.code.java.mods.moneymoneymoney.item;

import com.jonah.code.java.mods.moneymoneymoney.capabilities.PlayerMoneyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MoneyItem extends Item {
    private final int value;

    public MoneyItem(Properties properties, int value) {
        super(properties.stacksTo(64));
        this.value = value;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            PlayerMoneyHelper.getMoneyData(player).ifPresent(data -> {
                data.addWallet(value);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.displayClientMessage(Component.translatable("message.moneymoneymoney.money_redeemed", value, data.getWalletBalance()), true);
            });
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
