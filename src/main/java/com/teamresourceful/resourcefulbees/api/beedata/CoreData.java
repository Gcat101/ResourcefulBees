package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneycombRegistry;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @param name the bees name or id also known as bee type is used as an id for the bee in multiple locations.
 * @param blockFlowers These blocks represent the flowers the bee uses for pollinating.
 * @param entityFlower An optional entity which can be used for pollinating. Defaulted to empty if no entity is provided.
 * @param maxTimeInHive Gets the maximum time a bee can spend in the hive before time modifications are performed.
 * @param lore lore provided by pack devs in bee json.
 */
public record CoreData(String name, String honeycomb, HolderSet<Block> blockFlowers, HolderSet<EntityType<?>> entityFlower, int maxTimeInHive, List<MutableComponent> lore) {
    /**
     * A default instance of {@link CoreData} that can be
     * used to minimize {@link NullPointerException}'s. This implementation sets the
     * bees name/type to "error".
     */
    public static final CoreData DEFAULT = new CoreData("error", "", HolderSet.direct(), HolderSet.direct(), BeeConstants.MAX_TIME_IN_HIVE, new ArrayList<>());

    /**
     * Returns a {@link Codec<CoreData>} that can be parsed to create a
     * {@link CoreData} object. The name value passed in is a fallback value
     * usually obtained from the bee json file name.
     * <i>Note: Name is synonymous with "bee type"</i>
     *
     * @param name The name (or "bee type") for the bee.
     * @return Returns a {@link Codec<CoreData>} that can be parsed to
     * create a {@link CoreData} object.
     */
    public static Codec<CoreData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(name),
                Codec.STRING.fieldOf("honeycombVariation").orElse("").forGetter(CoreData::honeycomb),
                HolderSetCodec.of(Registry.BLOCK).fieldOf("flower").orElse(HolderSet.direct(Block::builtInRegistryHolder, Blocks.POPPY)).forGetter(CoreData::blockFlowers),
                HolderSetCodec.of(Registry.ENTITY_TYPE).fieldOf("entityFlower").orElse(HolderSet.direct()).forGetter(CoreData::entityFlower),
                Codec.intRange(600, Integer.MAX_VALUE).fieldOf("maxTimeInHive").orElse(2400).forGetter(CoreData::maxTimeInHive),
                CodecExtras.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson).listOf().fieldOf("lore").orElse(Lists.newArrayList()).forGetter(CoreData::lore)
        ).apply(instance, CoreData::new));
    }

    public boolean isEntityPresent() {
        return entityFlower().size() > 0;
    }


    /**
     * Returns an {@link Optional}&lt;{@link OutputVariation}&gt; object containing information regarding the
     * honeycomb a bee produces if it is specified to produce one.
     *
     * Omitting this object from the bee json results in a default object where the bee
     * <b>does not</b> produce a honeycomb.
     *
     * @return Returns an {@link Optional}&lt;{@link OutputVariation}&gt; with the contained data being immutable.
     */
    public Optional<OutputVariation> getHoneycombData() {
        return Optional.ofNullable(HoneycombRegistry.getOutputVariation(honeycomb()));
    }
}
