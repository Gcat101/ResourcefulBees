package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.fluids.NormalHoneyFluidType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFluids {

    private ModFluids() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ResourcefulBees.MOD_ID);

    //Used for registering to its own registry to let us easily query it later.
    public static final DeferredRegister<Fluid> STILL_HONEY_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ResourcefulBees.MOD_ID);
    public static final DeferredRegister<Fluid> FLOWING_HONEY_FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ResourcefulBees.MOD_ID);

    public static void initializeRegistries(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        STILL_HONEY_FLUIDS.register(bus);
        FLOWING_HONEY_FLUIDS.register(bus);
    }

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(HONEY, HONEY_STILL, HONEY_FLOWING)
                .bucket(ModItems.HONEY_FLUID_BUCKET)
                .block(ModBlocks.HONEY_FLUID_BLOCK)
                .tickRate(20);
    }

    public static final RegistryObject<FluidType> HONEY = FLUID_TYPES.register("honey", NormalHoneyFluidType::of);

    public static final RegistryObject<FlowingFluid> HONEY_STILL = FLUIDS.register("honey", () ->
            new ForgeFlowingFluid.Source(makeProperties())
    );
    public static final RegistryObject<FlowingFluid> HONEY_FLOWING = FLUIDS.register("honey_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeProperties())
    );
}
