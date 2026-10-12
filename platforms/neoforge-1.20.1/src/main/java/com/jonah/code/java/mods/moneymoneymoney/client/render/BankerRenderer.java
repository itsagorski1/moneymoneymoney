package com.jonah.code.java.mods.moneymoneymoney.client.render;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import com.jonah.code.java.mods.moneymoneymoney.entity.BankerEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BankerRenderer extends MobRenderer<BankerEntity, VillagerModel<BankerEntity>> {
    private static final ResourceLocation BANKER_TEXTURE =
            new ResourceLocation(MoneyMoneyMoneyMod.MOD_ID, "textures/entity/banker.png");

    public BankerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(BankerEntity entity) {
        return BANKER_TEXTURE;
    }

    @Override
    protected boolean shouldShowName(BankerEntity entity) {
        return true;
    }
}
