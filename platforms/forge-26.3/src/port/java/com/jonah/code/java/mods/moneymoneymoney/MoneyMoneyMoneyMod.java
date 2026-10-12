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
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(MoneyMoneyMoneyMod.MOD_ID)
public final class MoneyMoneyMoneyMod {
    public static final String MOD_ID = "moneymoneymoney";
    private static final String WALLET_KEY = MOD_ID + ".wallet";
    private static final String BANK_KEY = MOD_ID + ".bank";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Block> BANK_TILES = block("bank_tiles", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(1.8F).sound(SoundType.STONE));
    private static final RegistryObject<Block> VAULT_PLATING = block("vault_plating", BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.NETHERITE_BLOCK));
    private static final RegistryObject<Block> TELLER_GLASS = BLOCKS.register("teller_glass", () -> new TransparentBlock(BlockBehaviour.Properties.of().setId(BLOCKS.key("teller_glass")).mapColor(MapColor.NONE).strength(0.3F).sound(SoundType.GLASS).noOcclusion()));
    private static final RegistryObject<Block> BANK_COMPUTER = block("bank_computer", BlockBehaviour.Properties.of().strength(2.0F).sound(SoundType.METAL).noOcclusion());
    private static final RegistryObject<Block> BANK_SEAT = block("bank_seat", BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.WOOD).noOcclusion());

    private static final RegistryObject<Item> ONE_DOLLAR = bill("one_dollar", 1);
    private static final RegistryObject<Item> FIVE_DOLLARS = bill("five_dollars", 5);
    private static final RegistryObject<Item> TEN_DOLLARS = bill("ten_dollars", 10);
    private static final RegistryObject<Item> TWENTY_DOLLARS = bill("twenty_dollars", 20);
    private static final RegistryObject<Item> FIFTY_DOLLARS = bill("fifty_dollars", 50);
    private static final RegistryObject<Item> BANK_TILES_ITEM = blockItem("bank_tiles", BANK_TILES);
    private static final RegistryObject<Item> VAULT_PLATING_ITEM = blockItem("vault_plating", VAULT_PLATING);
    private static final RegistryObject<Item> TELLER_GLASS_ITEM = blockItem("teller_glass", TELLER_GLASS);
    private static final RegistryObject<Item> BANK_COMPUTER_ITEM = blockItem("bank_computer", BANK_COMPUTER);
    private static final RegistryObject<Item> BANK_SEAT_ITEM = blockItem("bank_seat", BANK_SEAT);

    public MoneyMoneyMoneyMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();
        BLOCKS.register(modBusGroup);
        ITEMS.register(modBusGroup);
        BuildCreativeModeTabContentsEvent.BUS.addListener(this::addCreativeItems);
        RegisterCommandsEvent.BUS.addListener(this::registerCommands);
        PlayerEvent.Clone.BUS.addListener(this::copyMoneyOnClone);
    }

    private static RegistryObject<Block> block(String name, BlockBehaviour.Properties properties) {
        return BLOCKS.register(name, () -> new Block(properties.setId(BLOCKS.key(name))));
    }

    private static RegistryObject<Item> bill(String name, int value) {
        return ITEMS.register(name, () -> new MoneyItem(new Item.Properties().setId(ITEMS.key(name)).stacksTo(64), value));
    }

    private static RegistryObject<Item> blockItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(ITEMS.key(name)).useBlockDescriptionPrefix()));
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
