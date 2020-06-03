package com.dungeonderps.resourcefulbees.client.render.entity;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SecondaryColorLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {
    private static final ResourceLocation BEE_COLLAR = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/custom/primary_layer.png");
    private static final ResourceLocation BEE_COLLAR_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/custom/bee_collar_angry.png");

    public SecondaryColorLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> rendererIn) {
        super(rendererIn);
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, CustomBeeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BeeData bee = entitylivingbaseIn.getBeeInfo();
        if (bee.isBeeColored()) {
            float[] secondaryColor = BeeInfo.getBeeColorAsFloat(bee.getSecondaryColor());
            ResourceLocation location = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConst.ENTITY_TEXTURES_DIR + bee.getSecondaryLayerTexture() + ".png");
            renderCutoutModel(this.getEntityModel(), location, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, secondaryColor[0], secondaryColor[1], secondaryColor[2]);
        }
    }
}
