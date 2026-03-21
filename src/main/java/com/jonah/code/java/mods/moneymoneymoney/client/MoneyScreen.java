package com.jonah.code.java.mods.moneymoneymoney.client;

import com.jonah.code.java.mods.moneymoneymoney.menu.MoneyMenu;
import com.jonah.code.java.mods.moneymoneymoney.network.ModMessages;
import com.jonah.code.java.mods.moneymoneymoney.network.MoneyActionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MoneyScreen extends AbstractContainerScreen<MoneyMenu> {
    private EditBox targetPlayerBox;
    private EditBox transferAmountBox;
    private EditBox depositAmountBox;
    private EditBox withdrawAmountBox;

    public MoneyScreen(MoneyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 248;
        imageHeight = 190;
        inventoryLabelY = imageHeight + 1000;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 12;
        titleLabelY = 10;

        targetPlayerBox = createBox(leftPos + 16, topPos + 58, 90, Component.translatable("screen.moneymoneymoney.target_player"));
        transferAmountBox = createBox(leftPos + 114, topPos + 58, 50, Component.translatable("screen.moneymoneymoney.amount"));
        depositAmountBox = createBox(leftPos + 16, topPos + 133, 60, Component.translatable("screen.moneymoneymoney.amount"));
        withdrawAmountBox = createBox(leftPos + 136, topPos + 133, 60, Component.translatable("screen.moneymoneymoney.amount"));

        addRenderableWidget(Button.builder(Component.translatable("screen.moneymoneymoney.transfer_button"), button ->
                sendTransfer()).bounds(leftPos + 172, topPos + 58, 60, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.moneymoneymoney.deposit_button"), button ->
                sendAction(MoneyActionPacket.Action.DEPOSIT, "", parsePositiveInt(depositAmountBox.getValue())))
                .bounds(leftPos + 16, topPos + 158, 80, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.moneymoneymoney.withdraw_button"), button ->
                sendAction(MoneyActionPacket.Action.WITHDRAW, "", parsePositiveInt(withdrawAmountBox.getValue())))
                .bounds(leftPos + 136, topPos + 158, 80, 20).build());

        setInitialFocus(targetPlayerBox);
    }

    private EditBox createBox(int x, int y, int width, Component hint) {
        EditBox box = new EditBox(font, x, y, width, 18, hint);
        box.setHint(hint);
        addRenderableWidget(box);
        return box;
    }

    private void sendTransfer() {
        sendAction(MoneyActionPacket.Action.TRANSFER, targetPlayerBox.getValue().trim(), parsePositiveInt(transferAmountBox.getValue()));
    }

    private void sendAction(MoneyActionPacket.Action action, String targetName, int amount) {
        if (amount <= 0) {
            return;
        }

        ModMessages.sendToServer(new MoneyActionPacket(action, targetName, amount));
    }

    private int parsePositiveInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.fill(x0, y0, x0 + imageWidth, y0 + imageHeight, 0xFF121A26);
        guiGraphics.fill(x0 + 6, y0 + 24, x0 + imageWidth - 6, y0 + 92, 0xFF1D2B3A);
        guiGraphics.fill(x0 + 6, y0 + 102, x0 + imageWidth - 6, y0 + imageHeight - 6, 0xFF243447);
        guiGraphics.fill(x0 + 8, y0 + 26, x0 + imageWidth - 8, y0 + 28, 0xFF8CCB5E);
        guiGraphics.fill(x0 + 8, y0 + 104, x0 + imageWidth - 8, y0 + 106, 0xFF6DB1FF);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0xF0F4F8, false);
        guiGraphics.drawString(font,
                Component.translatable("screen.moneymoneymoney.wallet_balance", menu.getWalletBalance()),
                16, 30, 0xF0F4F8, false);
        guiGraphics.drawString(font,
                Component.translatable("screen.moneymoneymoney.bank_balance", menu.getBankBalance()),
                16, 42, 0xF0F4F8, false);
        guiGraphics.drawString(font, Component.translatable("screen.moneymoneymoney.target_label"), 16, 48, 0xC9D4DF, false);
        guiGraphics.drawString(font, Component.translatable("screen.moneymoneymoney.amount_label"), 114, 48, 0xC9D4DF, false);
        guiGraphics.drawString(font, Component.translatable("screen.moneymoneymoney.transfer_heading"), 16, 78, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.translatable("screen.moneymoneymoney.bank_heading"), 16, 116, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.translatable("screen.moneymoneymoney.deposit_label"), 16, 122, 0xC9D4DF, false);
        guiGraphics.drawString(font, Component.translatable("screen.moneymoneymoney.withdraw_label"), 136, 122, 0xC9D4DF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
