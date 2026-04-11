package com.jonah.code.java.mods.moneymoneymoney.command;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.capabilities.IPlayerMoneyData;
import com.jonah.code.java.mods.moneymoneymoney.capabilities.PlayerMoneyHelper;
import com.jonah.code.java.mods.moneymoneymoney.menu.MoneyMenu;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoneyMoneyMoneyMod.MOD_ID)
public final class MoneyCommands {
    private MoneyCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("money")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(Commands.literal("balance").executes(MoneyCommands::showBalance))
                .then(Commands.literal("menu").executes(context -> {
                    MoneyMenu.open(context.getSource().getPlayerOrException());
                    return 1;
                }))
                .then(Commands.literal("deposit")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> deposit(context, IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("withdraw")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> withdraw(context, IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("transfer")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(context -> transfer(context,
                                                StringArgumentType.getString(context, "player"),
                                                IntegerArgumentType.getInteger(context, "amount"))))))
                .then(Commands.literal("mint")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> mint(context, IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int showBalance(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerMoneyHelper.getMoneyData(player).ifPresent(data -> player.sendSystemMessage(balanceMessage(data)));
        return 1;
    }

    private static int deposit(CommandContext<CommandSourceStack> context, int amount) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerMoneyHelper.getMoneyData(player).ifPresent(data -> {
            if (data.depositToBank(amount)) {
                player.sendSystemMessage(Component.translatable("message.moneymoneymoney.deposit_success", amount));
            } else {
                player.sendSystemMessage(Component.translatable("message.moneymoneymoney.deposit_failed"));
            }
        });
        return 1;
    }

    private static int withdraw(CommandContext<CommandSourceStack> context, int amount) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerMoneyHelper.getMoneyData(player).ifPresent(data -> {
            if (data.withdrawFromBank(amount)) {
                player.sendSystemMessage(Component.translatable("message.moneymoneymoney.withdraw_success", amount));
            } else {
                player.sendSystemMessage(Component.translatable("message.moneymoneymoney.withdraw_failed"));
            }
        });
        return 1;
    }

    private static int transfer(CommandContext<CommandSourceStack> context, String playerName, int amount) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer target = sender.server.getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.player_not_found", playerName));
            return 0;
        }

        if (sender.getUUID().equals(target.getUUID())) {
            sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_self"));
            return 0;
        }

        PlayerMoneyHelper.getMoneyData(sender).ifPresent(senderData ->
                PlayerMoneyHelper.getMoneyData(target).ifPresent(targetData -> {
                    if (senderData.removeWallet(amount)) {
                        targetData.addWallet(amount);
                        sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_success", amount, target.getGameProfile().getName()));
                        target.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_received", amount, sender.getGameProfile().getName()));
                    } else {
                        sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_failed"));
                    }
                }));
        return 1;
    }

    private static int mint(CommandContext<CommandSourceStack> context, int amount) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        giveBills(player, amount);
        player.sendSystemMessage(Component.translatable("message.moneymoneymoney.mint_success", amount));
        return 1;
    }

    private static Component balanceMessage(IPlayerMoneyData data) {
        return Component.translatable("message.moneymoneymoney.balance", data.getWalletBalance(), data.getBankBalance());
    }

    private static void giveBills(ServerPlayer player, int amount) {
        int remaining = amount;
        remaining = giveBillStacks(player, remaining, 50, ModItems.FIFTY_DOLLARS.get());
        remaining = giveBillStacks(player, remaining, 20, ModItems.TWENTY_DOLLARS.get());
        remaining = giveBillStacks(player, remaining, 10, ModItems.TEN_DOLLARS.get());
        remaining = giveBillStacks(player, remaining, 5, ModItems.FIVE_DOLLARS.get());
        giveBillStacks(player, remaining, 1, ModItems.ONE_DOLLAR.get());
    }

    private static int giveBillStacks(ServerPlayer player, int remainingAmount, int billValue, Item billItem) {
        int billCount = remainingAmount / billValue;
        while (billCount > 0) {
            int stackSize = Math.min(64, billCount);
            player.getInventory().placeItemBackInInventory(new ItemStack(billItem, stackSize));
            billCount -= stackSize;
        }
        return remainingAmount % billValue;
    }
}
