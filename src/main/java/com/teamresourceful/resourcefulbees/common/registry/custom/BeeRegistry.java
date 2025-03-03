package com.teamresourceful.resourcefulbees.common.registry.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.RegisterBeeEvent;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefullib.common.utils.RandomCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;

public final class BeeRegistry implements IBeeRegistry {

    private static final BeeRegistry INSTANCE = new BeeRegistry();
    private static final Map<String, JsonObject> RAW_DATA = new LinkedHashMap<>();
    private static final Map<String, CustomBeeData> CUSTOM_DATA = new LinkedHashMap<>();
    private static final Map<Pair<String, String>, RandomCollection<BeeFamily>> FAMILY_TREE = new LinkedHashMap<>();

    private BeeRegistry() {
        // Single instanced classes do not need to be able to be extended
    }

    /**
     * Returns an instance of the {@link BeeRegistry} for accessing data from the registry.
     * The bee Registry is a central point for getting any bee data pertinent to
     * <i>Resourceful Bees</i>. The registry contains a cache of {@link JsonObject}'s and
     * {@link CustomBeeData} objects for all bees registered to the mod. The registry also
     * contains the spawn rules and breeding rules for bees created by <i>Resourceful Bees</i>.
     *
     * @return Returns an instance of the {@link BeeRegistry} for accessing data from the registry.
     */
    public static BeeRegistry getRegistry() {
        return INSTANCE;
    }

    public static boolean containsBeeType(String beeType) {
        return CUSTOM_DATA.containsKey(beeType);
    }

    public Map<Pair<String, String>, RandomCollection<BeeFamily>> getFamilyTree() {
        return FAMILY_TREE;
    }

    /**
     * Returns a {@link CustomBeeData} object for the given bee type.
     *
     * @param beeType Bee type for which BeeData is requested.
     * @return Returns a {@link CustomBeeData} object for the given bee type.
     */
    public CustomBeeData getBeeData(String beeType) {
        return CUSTOM_DATA.getOrDefault(beeType, CustomBeeData.DEFAULT);
    }

    /**
     * This method first iterates over the internal map of raw data and populates
     * the registry with new {@link CustomBeeData} objects that have been parsed
     * using the codecs. A {@link RegisterBeeEvent} is then posted for other mods
     * to register {@link CustomBeeData} objects they want Resourceful Bees to have
     * compatibility with. Post Init methods are then ran on BeeFamilies to ensure
     * data has populated correctly. Finally, the family trees and spawnable biomes
     * maps are constructed.
     */
    public void regenerateCustomBeeData(RegistryAccess access) {
        DynamicOps<JsonElement> ops = access == null ? JsonOps.INSTANCE : RegistryOps.create(JsonOps.INSTANCE, access);

        RAW_DATA.forEach((s, jsonObject) -> CUSTOM_DATA.compute(s, (s1, customBeeDataCodec) ->
                CustomBeeData.codec(s).parse(ops, jsonObject)
                .getOrThrow(false, s2 -> ResourcefulBees.LOGGER.error("Could not create Custom Bee Data for {} bee", s))));
        //MinecraftForge.EVENT_BUS.post(new RegisterBeeEvent(beeData));
        BeeRegistry.buildFamilyTree();
    }

    /**
     * Returns a BeeData object for the given bee type.
     *
     * @param bee Bee type for which BeeData is requested.
     * @return Returns a BeeData object for the given bee type.
     */
    @Override
    public JsonObject getRawBeeData(String bee) {
        return RAW_DATA.get(bee);
    }

    /**
     * Returns true if supplied parents can make a child bee.
     *
     * @param parent1 Bee type for Parent 1.
     * @param parent2 Bee type for parent 2.
     * @return Returns true/false if parents can breed.
     */
    public boolean canParentsBreed(String parent1, String parent2) {
        return FAMILY_TREE.containsKey(BeeInfoUtils.sortParents(parent1, parent2));
    }

    /**
     * Returns the a weighted random bee type based on the supplied parents.
     *
     * @param parent1 Bee type for Parent 1.
     * @param parent2 Bee type for parent 2.
     * @return Returns a weighted random bee type as a string.
     */
    public BeeFamily getWeightedChild(String parent1, String parent2) {
        return FAMILY_TREE.get(BeeInfoUtils.sortParents(parent1, parent2)).next();
    }

    /**
     * Returns the adjusted weight for the supplied child's data.
     * The returned value is an adjusted percentage in the range of 0 - 100 represented as a double.
     * This value is calculated based on the weighting of all possible children the supplied child's parents can have.
     *
     * @param beeFamily BeeData object for the child.
     * @return Returns random bee type as a string.
     */
    public double getAdjustedWeightForChild(BeeFamily beeFamily) {
        return FAMILY_TREE.get(beeFamily.parents()).getAdjustedWeight(beeFamily.weight());
    }

    /**
     * Registers the supplied Bee Type and associated data, in the form
     * of a {@link JsonObject}, to the mod.
     *
     * @param beeType The Bee Type of the bee being registered.
     * @param beeData The raw BeeData of the bee being registered
     */
    public void cacheRawBeeData(String beeType, JsonObject beeData) {
        RAW_DATA.computeIfAbsent(beeType.toLowerCase(Locale.ENGLISH).replace(" ", "_"), s -> Objects.requireNonNull(beeData));
    }

    /**
     * Returns an unmodifiable copy of the internal {@link JsonObject} map representing
     * the raw json data. This is useful for iterating over all bees without worry of
     * changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns unmodifiable copy of the internal {@link JsonObject} map representing
     * the raw json data.
     */
    public Map<String, JsonObject> getRawBees() {
        return Collections.unmodifiableMap(RAW_DATA);
    }

    /**
     * Returns an unmodifiable copy of the internal {@link CustomBeeData} map.
     * This is useful for iterating over all bees without worry of changing registry data
     * as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable copy of the internal {@link CustomBeeData} map.
     */
    public Map<String, CustomBeeData> getBees() {
        return Collections.unmodifiableMap(CUSTOM_DATA);
    }

    /**
     * A helper method that returns an unmodifiable set of the values contained in the internal
     * {@link CustomBeeData} map. This is useful for iterating over all bees without
     * worry of changing registry data as the objects contained in the map are immutable.
     *
     * @return Returns an unmodifiable set of the values contained in the internal
     * {@link CustomBeeData} map
     */
    public Set<CustomBeeData> getSetOfBees() {
        return Set.copyOf(CUSTOM_DATA.values());
    }

    /**
     * A helper method that returns a stream using the {@link BeeRegistry#getSetOfBees()} method.
     */
    public Stream<CustomBeeData> getStreamOfBees() {
        return Set.copyOf(CUSTOM_DATA.values()).stream();
    }

    //region Setup

    private static void buildFamilyTree() {
        FAMILY_TREE.clear();
        CUSTOM_DATA.values().stream()
                .filter(customBeeData -> customBeeData.breedData().hasParents())
                .flatMap(customBeeData -> customBeeData.breedData().families().stream())
                .filter(BeeFamily::hasValidParents)
                .forEach(BeeRegistry::addBreedPairToFamilyTree);
    }

    private static void addBreedPairToFamilyTree(BeeFamily beeFamily) {
        FAMILY_TREE.computeIfAbsent(beeFamily.parents(), k -> new RandomCollection<>()).add(beeFamily.weight(), beeFamily);
    }
    //endregion
}
