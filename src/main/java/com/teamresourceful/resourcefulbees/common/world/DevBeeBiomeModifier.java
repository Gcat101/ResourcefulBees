package com.teamresourceful.resourcefulbees.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.ArrayList;
import java.util.List;

public record DevBeeBiomeModifier(
        List<HolderSet<Biome>> whitelist,
        List<HolderSet<Biome>> blacklist,
        MobSpawnSettings.SpawnerData spawns
) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (Boolean.FALSE.equals(CommonConfig.ENABLE_DEV_BEES.get())) return;
        if (phase.equals(Phase.ADD) && isInList(whitelist(), biome) && !isInList(blacklist(), biome)) {
            builder.getMobSpawnSettings().addSpawn(spawns().type.getCategory(), spawns());
        }
    }

    private static boolean isInList(List<HolderSet<Biome>> biomes, Holder<Biome> checkingBiome) {
        for (HolderSet<Biome> biome : biomes) {
            if (biome.contains(checkingBiome)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.DEV_SPAWN_MODIFIER.get();
    }

    public static Codec<DevBeeBiomeModifier> makeCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Biome.LIST_CODEC.listOf().fieldOf("whitelist").forGetter(DevBeeBiomeModifier::whitelist),
                Biome.LIST_CODEC.listOf().fieldOf("blacklist").orElse(new ArrayList<>()).forGetter(DevBeeBiomeModifier::blacklist),
                MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawns").forGetter(DevBeeBiomeModifier::spawns)
        ).apply(instance, DevBeeBiomeModifier::new));
    }
}
