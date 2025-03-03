package com.teamresourceful.resourcefulbees.common.recipe.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import com.teamresourceful.resourcefullib.common.utils.RandomCollection;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record BreederRecipe(ResourceLocation id, BreederPair parent1, BreederPair parent2, Optional<Ingredient> input, RandomCollection<BreederOutput> outputs, int time) implements CodecRecipe<Container> {

    public static final Codec<RandomCollection<BreederOutput>> RANDOM_COLLECTION_CODEC = CodecExtras.set(BreederOutput.CODEC).comapFlatMap(BreederOutput::convertToRandomCollection, BreederOutput::convertToSet);

    public static Codec<BreederRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
            RecordCodecBuilder.point(id),
            BreederPair.CODEC.fieldOf("parent1").forGetter(BreederRecipe::parent1),
            BreederPair.CODEC.fieldOf("parent2").forGetter(BreederRecipe::parent2),
            IngredientCodec.CODEC.optionalFieldOf("input").forGetter(BreederRecipe::input),
            BreederRecipe.RANDOM_COLLECTION_CODEC.fieldOf("outputs").forGetter(BreederRecipe::outputs),
            Codec.intRange(100, 72000).fieldOf("time").orElse(1200).forGetter(BreederRecipe::time)
        ).apply(instance, BreederRecipe::new));
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level level) {
        return parent1.matches(inventory, 0) && parent2.matches(inventory, 2) && (input.isEmpty() || input.get().test(inventory.getItem(4)));
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BREEDER_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipeTypes.BREEDER_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, parent1.parent(), parent1.feedItem(), parent2.parent(), parent2.feedItem());
    }

    public record BreederPair(Ingredient parent, Optional<String> displayEntity, Ingredient feedItem, Optional<ItemStack> returnItem){
        public static final Codec<BreederPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                IngredientCodec.CODEC.fieldOf("parent").forGetter(BreederPair::parent),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederPair::displayEntity),
                IngredientCodec.CODEC.fieldOf("feedItem").forGetter(BreederPair::feedItem),
                ItemStackCodec.CODEC.optionalFieldOf("returnItem").forGetter(BreederPair::returnItem)
        ).apply(instance, BreederPair::new));

        public boolean matches(Container inventory, int offset) {
            return parent.test(inventory.getItem(offset)) && feedItem.test(inventory.getItem(offset+1));
        }
    }

    public record BreederOutput(ItemStack output, Optional<String> displayEntity, double weight, double chance){
        public static final Codec<BreederOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStackCodec.CODEC.fieldOf("output").forGetter(BreederOutput::output),
                Codec.STRING.optionalFieldOf("entity").forGetter(BreederOutput::displayEntity),
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BreederOutput::weight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BreederOutput::chance)
        ).apply(instance, BreederOutput::new));

        private static DataResult<RandomCollection<BreederOutput>> convertToRandomCollection(Set<BreederOutput> set) {
            return DataResult.success(set.stream().collect(RandomCollection.getCollector(BreederOutput::weight)));
        }

        private static Set<BreederOutput> convertToSet(RandomCollection<BreederOutput> randomCollection) {
            return new HashSet<>(randomCollection.getMap().values());
        }
    }
}
