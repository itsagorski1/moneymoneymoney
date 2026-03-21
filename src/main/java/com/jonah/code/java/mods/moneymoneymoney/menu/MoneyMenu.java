package com.jonah.code.java.mods.moneymoneymoney.menu;

import com.jonah.code.java.mods.moneymoneymoney.capabilities.PlayerMoneyHelper;
import com.jonah.code.java.mods.moneymoneymoney.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

public class MoneyMenu extends AbstractContainerMenu {
    private final Player player;
    private final DataSlot walletBalance;
    private final DataSlot bankBalance;

    public MoneyMenu(int containerId, Inventory inventory, FriendlyByteBuf ignored) {
        this(containerId, inventory);
    }

    public MoneyMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, DataSlot.standalone(), DataSlot.standalone());
    }

    private MoneyMenu(int containerId, Inventory inventory, DataSlot walletBalance, DataSlot bankBalance) {
        super(ModMenus.MONEY_MENU.get(), containerId);
        this.player = inventory.player;
        this.walletBalance = walletBalance;
        this.bankBalance = bankBalance;

        addDataSlot(this.walletBalance);
        addDataSlot(this.bankBalance);
        refreshBalances();
    }

    public static void open(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                (containerId, inventory, ignored) -> new MoneyMenu(containerId, inventory),
                Component.translatable("screen.moneymoneymoney.money_menu")));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public void broadcastChanges() {
        refreshBalances();
        super.broadcastChanges();
    }

    public int getWalletBalance() {
        return walletBalance.get();
    }

    public int getBankBalance() {
        return bankBalance.get();
    }

    private void refreshBalances() {
        PlayerMoneyHelper.getMoneyData(player).ifPresent(data -> {
            walletBalance.set(data.getWalletBalance());
            bankBalance.set(data.getBankBalance());
        });
    }
}
