package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.honey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.traits.PotionEffect;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyEffect;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import static com.teamresourceful.resourcefulbees.client.components.beepedia.selection.BeeEntry.SLOT_TEXTURE;

public class EffectEntry extends ListEntry {

    private final MobEffect effect;
    private final int strength;
    private final Component durationText;
    private final float chance;

    public EffectEntry(HoneyEffect effect) {
        this.effect = effect.effect();
        this.strength = effect.strength();
        this.durationText = Component.literal(String.format("(%02d:%02d)", (effect.duration() / 20) / 60, (effect.duration() / 20) % 60));
        this.chance = effect.chance();
    }

    public EffectEntry(PotionEffect effect) {
        this.effect = effect.effect();
        this.strength = effect.strength();
        this.durationText = Component.literal("Time depends on difficulty.");
        this.chance = 0f;
    }

    public EffectEntry(MobEffect effect) {
        this.effect = effect;
        this.strength = 0;
        this.durationText = Component.literal("Immune no matter the strength.");
        this.chance = 0f;
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        try (var ignored = new CloseablePoseStack(stack)) {
            ignored.translate(left, top, 0);
            Minecraft instance = Minecraft.getInstance();
            Font font = instance.font;
            MobEffectTextureManager textureManager = instance.getMobEffectTextures();

            TextureAtlasSprite sprite = textureManager.get(this.effect);
            ClientUtils.bindTexture(sprite.atlas().location());
            Gui.blit(stack, 2, 1, 0, 18, 18, sprite);

            ClientUtils.bindTexture(SLOT_TEXTURE);
            Gui.blit(stack, 1, 0, 0, 0, 20, 20, 20, 60);

            int color = effect.getCategory() == MobEffectCategory.HARMFUL ? 16733525 : 5592575;
            MutableComponent component = effect.getDisplayName().copy();
            if (this.strength > 0) component.append(" " + ModUtils.createRomanNumeral(this.strength));
            GuiComponent.drawString(stack, font, component, 24, 1, color);
            MutableComponent text = durationText.copy();
            if (chance < 1 && chance > 0) text.append(" " + DecimalFormat.getPercentInstance().format(chance));

            GuiComponent.drawString(stack, font, text, 24, 11, 11184810);
        }
    }
}