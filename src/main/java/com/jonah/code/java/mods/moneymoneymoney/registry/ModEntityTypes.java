package com.jonah.code.java.mods.moneymoneymoney.registry;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.entity.BankerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MoneyMoneyMoneyMod.MOD_ID);

    public static final RegistryObject<EntityType<BankerEntity>> BANKER =
            ENTITY_TYPES.register("banker", () ->
                    EntityType.Builder.of(BankerEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(MoneyMoneyMoneyMod.MOD_ID + ":banker"));

    private ModEntityTypes() {
    }
}
