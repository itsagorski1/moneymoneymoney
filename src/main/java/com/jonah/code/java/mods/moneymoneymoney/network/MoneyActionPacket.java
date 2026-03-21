package com.jonah.code.java.mods.moneymoneymoney.network;

import com.jonah.code.java.mods.moneymoneymoney.capabilities.IPlayerMoneyData;
import com.jonah.code.java.mods.moneymoneymoney.capabilities.PlayerMoneyHelper;
import com.jonah.code.java.mods.moneymoneymoney.menu.MoneyMenu;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class MoneyActionPacket {
    private final Action action;
    private final String targetName;
    private final int amount;

    public MoneyActionPacket(Action action, String targetName, int amount) {
        this.action = action;
        this.targetName = targetName;
        this.amount = amount;
    }

    public static void encode(MoneyActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.action);
        buffer.writeUtf(packet.targetName);
        buffer.writeInt(packet.amount);
    }

    public static MoneyActionPacket decode(FriendlyByteBuf buffer) {
        return new MoneyActionPacket(buffer.readEnum(Action.class), buffer.readUtf(64), buffer.readInt());
    }

    public static void handle(MoneyActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null || !(sender.containerMenu instanceof MoneyMenu)) {
                return;
            }

            if (packet.amount <= 0) {
                sender.displayClientMessage(Component.translatable("message.moneymoneymoney.invalid_amount"), false);
                return;
            }

            switch (packet.action) {
                case DEPOSIT -> deposit(sender, packet.amount);
                case WITHDRAW -> withdraw(sender, packet.amount);
                case TRANSFER -> transfer(sender, packet.targetName, packet.amount);
            }
        });
        context.setPacketHandled(true);
    }

    private static void deposit(ServerPlayer sender, int amount) {
        PlayerMoneyHelper.getMoneyData(sender).ifPresent(data -> {
            if (data.depositToBank(amount)) {
                sender.displayClientMessage(Component.translatable("message.moneymoneymoney.deposit_success", amount), false);
            } else {
                sender.displayClientMessage(Component.translatable("message.moneymoneymoney.deposit_failed"), false);
            }
        });
    }

    private static void withdraw(ServerPlayer sender, int amount) {
        PlayerMoneyHelper.getMoneyData(sender).ifPresent(data -> {
            if (data.withdrawFromBank(amount)) {
                sender.displayClientMessage(Component.translatable("message.moneymoneymoney.withdraw_success", amount), false);
            } else {
                sender.displayClientMessage(Component.translatable("message.moneymoneymoney.withdraw_failed"), false);
            }
        });
    }

    private static void transfer(ServerPlayer sender, String targetName, int amount) {
        ServerPlayer target = sender.server.getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            sender.displayClientMessage(Component.translatable("message.moneymoneymoney.player_not_found", targetName), false);
            return;
        }

        if (target.getUUID().equals(sender.getUUID())) {
            sender.displayClientMessage(Component.translatable("message.moneymoneymoney.transfer_self"), false);
            return;
        }

        PlayerMoneyHelper.getMoneyData(sender).ifPresent(senderData ->
                PlayerMoneyHelper.getMoneyData(target).ifPresent(targetData -> performTransfer(sender, senderData, target, targetData, amount)));
    }

    private static void performTransfer(ServerPlayer sender, IPlayerMoneyData senderData, ServerPlayer target,
            IPlayerMoneyData targetData, int amount) {
        if (!senderData.removeWallet(amount)) {
            sender.displayClientMessage(Component.translatable("message.moneymoneymoney.transfer_failed"), false);
            return;
        }

        targetData.addWallet(amount);
        sender.displayClientMessage(Component.translatable("message.moneymoneymoney.transfer_success", amount, target.getGameProfile().getName()), false);
        target.displayClientMessage(Component.translatable("message.moneymoneymoney.transfer_received", amount, sender.getGameProfile().getName()), false);
    }

    public enum Action {
        TRANSFER,
        DEPOSIT,
        WITHDRAW
    }
}
