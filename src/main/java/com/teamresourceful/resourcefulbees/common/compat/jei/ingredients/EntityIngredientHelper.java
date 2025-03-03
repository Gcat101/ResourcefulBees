package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityIngredientHelper implements IIngredientHelper<EntityIngredient> {

    @Override
    public @NotNull IIngredientType<EntityIngredient> getIngredientType() {
        return JEICompat.ENTITY_INGREDIENT;
    }


    @NotNull
    @Override
    public String getDisplayName(EntityIngredient entityIngredient) {
        return I18n.get(entityIngredient.getEntityType().getDescriptionId());
    }

    @Override
    public @NotNull String getUniqueId(@NotNull EntityIngredient entityIngredient, @NotNull UidContext context) {
        Entity entity = entityIngredient.getEntity();
        if (entity == null) return "entity:error";
        String id = entity.getEncodeId();
        return id == null ? "entity:error" : "entity:" + id;
    }

    @NotNull
    @Override
    public ItemStack getCheatItemStack(EntityIngredient ingredient) {
        SpawnEggItem spawnEggItem = ForgeSpawnEggItem.fromEntityType(ingredient.getEntityType());
        return spawnEggItem == null ? ItemStack.EMPTY : spawnEggItem.getDefaultInstance();
    }

    @NotNull
    @Override
    public String getWildcardId(@NotNull EntityIngredient entityIngredient) {
        return this.getUniqueId(entityIngredient, UidContext.Ingredient);
    }

    @Override
    public @NotNull ResourceLocation getResourceLocation(EntityIngredient ingredient) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(ingredient.getEntityType());
        if (id == null) return new ResourceLocation("error");
        return id;
    }

    @NotNull
    @Override
    public EntityIngredient copyIngredient(@NotNull EntityIngredient entityIngredient) {
        return entityIngredient;
    }

    @NotNull
    @Override
    public String getErrorInfo(@Nullable EntityIngredient entityIngredient) {
        return entityIngredient == null ? "null" : entityIngredient.toString();
    }
}
