package com.jonah.code.java.mods.moneymoneymoney;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(MoneyMoneyMoneyMod.MOD_ID)
public final class MoneyMoneyMoneyMod {
    public static final String MOD_ID = "moneymoneymoney";
    private static final String WALLET_KEY = MOD_ID + ".wallet";
    private static final String BANK_KEY = MOD_ID + ".bank";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    private static final DeferredBlock<Block> BANK_TILES = BLOCKS.registerSimpleBlock("bank_tiles", p -> p.mapColor(MapColor.QUARTZ).strength(1.8F).sound(SoundType.STONE));
    private static final DeferredBlock<Block> VAULT_PLATING = BLOCKS.registerSimpleBlock("vault_plating", p -> p.mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.NETHERITE_BLOCK));
    private static final DeferredBlock<TransparentBlock> TELLER_GLASS = BLOCKS.registerBlock("teller_glass", TransparentBlock::new, p -> p.mapColor(MapColor.NONE).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
    private static final DeferredBlock<Block> BANK_COMPUTER = BLOCKS.registerSimpleBlock("bank_computer", p -> p.strength(2.0F).sound(SoundType.METAL).noOcclusion());
    private static final DeferredBlock<Block> BANK_SEAT = BLOCKS.registerSimpleBlock("bank_seat", p -> p.strength(1.5F).sound(SoundType.WOOD).noOcclusion());

    private static final DeferredItem<MoneyItem> ONE_DOLLAR = bill("one_dollar", 1);
    private static final DeferredItem<MoneyItem> FIVE_DOLLARS = bill("five_dollars", 5);
    private static final DeferredItem<MoneyItem> TEN_DOLLARS = bill("ten_dollars", 10);
    private static final DeferredItem<MoneyItem> TWENTY_DOLLARS = bill("twenty_dollars", 20);
    private static final DeferredItem<MoneyItem> FIFTY_DOLLARS = bill("fifty_dollars", 50);
    private static final DeferredItem<BlockItem> BANK_TILES_ITEM = ITEMS.registerSimpleBlockItem(BANK_TILES);
    private static final DeferredItem<BlockItem> VAULT_PLATING_ITEM = ITEMS.registerSimpleBlockItem(VAULT_PLATING);
    private static final DeferredItem<BlockItem> TELLER_GLASS_ITEM = ITEMS.registerSimpleBlockItem(TELLER_GLASS);
    private static final DeferredItem<BlockItem> BANK_COMPUTER_ITEM = ITEMS.registerSimpleBlockItem(BANK_COMPUTER);
    private static final DeferredItem<BlockItem> BANK_SEAT_ITEM = ITEMS.registerSimpleBlockItem(BANK_SEAT);

    public MoneyMoneyMoneyMod(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::addCreativeItems);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::copyMoneyOnClone);
    }

    private static DeferredItem<MoneyItem> bill(String name, int value) {
        return ITEMS.registerItem(name, properties -> new MoneyItem(properties.stacksTo(64), value));
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ONE_DOLLAR); event.accept(FIVE_DOLLARS); event.accept(TEN_DOLLARS); event.accept(TWENTY_DOLLARS); event.accept(FIFTY_DOLLARS);
        } else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(BANK_TILES_ITEM); event.accept(VAULT_PLATING_ITEM); event.accept(TELLER_GLASS_ITEM);
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(BANK_COMPUTER_ITEM); event.accept(BANK_SEAT_ITEM);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("walletbalance").requires(s -> s.getEntity() instanceof ServerPlayer).executes(c -> balance(c.getSource().getPlayerOrException())));
        dispatcher.register(Commands.literal("money").requires(s -> s.getEntity() instanceof ServerPlayer)
                .then(Commands.literal("balance").executes(c -> balance(c.getSource().getPlayerOrException())))
                .then(Commands.literal("menu").executes(c -> balance(c.getSource().getPlayerOrException())))
                .then(Commands.literal("deposit").then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(c -> deposit(c.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(c, "amount")))))
                .then(Commands.literal("withdraw").then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(c -> withdraw(c.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(c, "amount")))))
                .then(Commands.literal("transfer").then(Commands.argument("player", StringArgumentType.word()).then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(c -> transfer(c.getSource().getPlayerOrException(), StringArgumentType.getString(c, "player"), IntegerArgumentType.getInteger(c, "amount"))))))
                .then(Commands.literal("mint").requires(s -> s.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)).then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(c -> mint(c.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(c, "amount"))))));
    }

    private void copyMoneyOnClone(PlayerEvent.Clone event) {
        setWallet(event.getEntity(), wallet(event.getOriginal()));
        setBank(event.getEntity(), bank(event.getOriginal()));
    }

    private static int balance(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("message.moneymoneymoney.balance", wallet(player), bank(player)));
        return 1;
    }

    private static int deposit(ServerPlayer player, int amount) {
        if (wallet(player) < amount) { player.sendSystemMessage(Component.translatable("message.moneymoneymoney.deposit_failed")); return 0; }
        setWallet(player, wallet(player) - amount); setBank(player, safeAdd(bank(player), amount));
        player.sendSystemMessage(Component.translatable("message.moneymoneymoney.deposit_success", amount)); return 1;
    }

    private static int withdraw(ServerPlayer player, int amount) {
        if (bank(player) < amount) { player.sendSystemMessage(Component.translatable("message.moneymoneymoney.withdraw_failed")); return 0; }
        setBank(player, bank(player) - amount); setWallet(player, safeAdd(wallet(player), amount));
        player.sendSystemMessage(Component.translatable("message.moneymoneymoney.withdraw_success", amount)); return 1;
    }

    private static int transfer(ServerPlayer sender, String name, int amount) {
        ServerPlayer target = sender.level().getServer().getPlayerList().getPlayerByName(name);
        if (target == null) { sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.player_not_found", name)); return 0; }
        if (target.getUUID().equals(sender.getUUID())) { sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_self")); return 0; }
        if (wallet(sender) < amount) { sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_failed")); return 0; }
        setWallet(sender, wallet(sender) - amount); setWallet(target, safeAdd(wallet(target), amount));
        sender.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_success", amount, target.getName().getString()));
        target.sendSystemMessage(Component.translatable("message.moneymoneymoney.transfer_received", amount, sender.getName().getString())); return 1;
    }

    private static int mint(ServerPlayer player, int amount) {
        int remaining = amount;
        remaining = give(player, remaining, 50, FIFTY_DOLLARS.get()); remaining = give(player, remaining, 20, TWENTY_DOLLARS.get());
        remaining = give(player, remaining, 10, TEN_DOLLARS.get()); remaining = give(player, remaining, 5, FIVE_DOLLARS.get()); give(player, remaining, 1, ONE_DOLLAR.get());
        player.sendSystemMessage(Component.translatable("message.moneymoneymoney.mint_success", amount)); return 1;
    }

    private static int give(ServerPlayer player, int amount, int value, Item item) {
        int count = amount / value;
        while (count > 0) { int size = Math.min(64, count); player.getInventory().add(new ItemStack(item, size)); count -= size; }
        return amount % value;
    }

    private static int wallet(Player player) { return player.getPersistentData().getInt(WALLET_KEY).orElse(0); }
    private static int bank(Player player) { return player.getPersistentData().getInt(BANK_KEY).orElse(0); }
    private static void setWallet(Player player, int value) { player.getPersistentData().putInt(WALLET_KEY, Math.max(0, value)); }
    private static void setBank(Player player, int value) { player.getPersistentData().putInt(BANK_KEY, Math.max(0, value)); }
    private static int safeAdd(int left, int right) { return (int) Math.min(Integer.MAX_VALUE, (long) left + right); }

    private static final class MoneyItem extends Item {
        private final int value;
        private MoneyItem(Properties properties, int value) { super(properties); this.value = value; }
        @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide()) {
                setWallet(player, safeAdd(wallet(player), value));
                if (!player.getAbilities().instabuild) stack.shrink(1);
                player.sendOverlayMessage(Component.translatable("message.moneymoneymoney.money_redeemed", value, wallet(player)));
            }
            return InteractionResult.SUCCESS;
        }
    }
}
