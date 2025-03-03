package com.teamresourceful.resourcefulbees.common.network.packets.server;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.item.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public record DimensionalBeesPacket(Map<ResourceKey<Level>, List<String>> bees) implements Packet<DimensionalBeesPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "dimensional_bees");
    public static final Handler HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DimensionalBeesPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<DimensionalBeesPacket> {

        @Override
        public void encode(DimensionalBeesPacket message, FriendlyByteBuf buffer) {
            buffer.writeMap(message.bees, FriendlyByteBuf::writeResourceKey, (buf, list) -> buf.writeCollection(list, FriendlyByteBuf::writeUtf));
        }

        @Override
        public DimensionalBeesPacket decode(FriendlyByteBuf buffer) {
            return new DimensionalBeesPacket(buffer.readMap(buf -> buf.readResourceKey(Registry.DIMENSION_REGISTRY), buf -> buf.readList(FriendlyByteBuf::readUtf)));
        }

        @Override
        public PacketContext handle(DimensionalBeesPacket message) {
            return (player, level) -> {
                DimensionalBeeHolder.DIMENSIONAL_BEES.clear();
                DimensionalBeeHolder.DIMENSIONAL_BEES.putAll(message.bees);
            };
        }
    }
}
