package com.jonah.code.java.mods.moneymoneymoney.registry;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.block.BankComputerBlock;
import com.jonah.code.java.mods.moneymoneymoney.block.BankSeatBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MoneyMoneyMoneyMod.MOD_ID);

    public static final RegistryObject<Block> BANK_TILES =
            BLOCKS.register("bank_tiles",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.QUARTZ)
                            .strength(1.8F)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> VAULT_PLATING =
            BLOCKS.register("vault_plating",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(5.0F, 6.0F)
                            .sound(SoundType.NETHERITE_BLOCK)
                            .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> TELLER_GLASS =
            BLOCKS.register("teller_glass",
                    () -> new GlassBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.NONE)
                            .strength(0.3F)
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .isValidSpawn((state, level, pos, entityType) -> false)
                            .isRedstoneConductor((state, level, pos) -> false)
                            .isSuffocating((state, level, pos) -> false)
                            .isViewBlocking((state, level, pos) -> false)));

    public static final RegistryObject<Block> BANK_COMPUTER =
            BLOCKS.register("bank_computer", BankComputerBlock::new);
    public static final RegistryObject<Block> BANK_SEAT =
            BLOCKS.register("bank_seat", BankSeatBlock::new);

    private ModBlocks() {
    }
}
