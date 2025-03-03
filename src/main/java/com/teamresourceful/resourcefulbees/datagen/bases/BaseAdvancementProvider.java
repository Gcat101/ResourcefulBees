package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAdvancementProvider implements DataProvider {

    public static final String TRANSLATIONS_PREFIX = "advancements.resourcefulbees.";
    public static final String TITLE_SUFFIX = ".title";
    public static final String DESCRIPTION_SUFFIX = ".description";

    private final DataGenerator generator;
    private final Map<ResourceLocation, Advancement> advancements = new HashMap<>();

    protected BaseAdvancementProvider(DataGenerator pGenerator) {
        this.generator = pGenerator;
    }

    public abstract void buildAdvancements();

    protected static Advancement createRootAdvancement(RegistryObject<Item> item, Component title, Component desc, ResourceLocation background, ItemPredicate predicate) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(),
                        title,
                        desc,
                        background,
                        FrameType.TASK,
                        false,
                        false,
                        false)
                .addCriterion("inventory_changed", inventoryTrigger(predicate))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/root"));
    }

    protected static Advancement.Builder createAdvancement(ItemStack item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item, Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement.Builder createAdvancement(RegistryObject<Item> item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(), Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement createSimpleAdvancement(RegistryObject<Item> item, String id, Advancement parent) {
        return createAdvancement(item, id, parent)
                .addCriterion("has_"+id, has(item.get()))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/"+id));
    }

    protected static Advancement.Builder createChallengeAchievement(ItemStack item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item, Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Advancement.Builder createChallengeAchievement(RegistryObject<Item> item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(), Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Advancement createSimpleChallengeAchievement(RegistryObject<Item> item, String id, Advancement parent) {
        return createChallengeAchievement(item, id, parent)
                .addCriterion("has_"+id, has(item.get()))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/"+id));
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike pItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> pTag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pTag).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicate) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, pPredicate);
    }

    public Advancement addAdvancement(Advancement advancement) {
        if (this.advancements.containsKey(advancement.getId()))
            throw new IllegalStateException("Duplicate advancement " + advancement.getId());
        this.advancements.put(advancement.getId(), advancement);
        return advancement;
    }

    @Override
    public void run(@NotNull CachedOutput cache) throws IOException {
        Path path = this.generator.getOutputFolder();

        buildAdvancements();

        for (Advancement advancement : this.advancements.values()) {
            Path path1 = createPath(path, advancement);
            DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), path1);
        }
    }

    private static Path createPath(Path path, Advancement advancement) {
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees Advancement Provider";
    }
}
