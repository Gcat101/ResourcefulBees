package com.teamresourceful.resourcefulbees.common.blockentity.base;

import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.server.SyncGuiPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ISyncableGUI extends MenuProvider {

    /**
     * Wrapper method for {@link BlockEntity#getBlockPos()} due to obfuscation
     * issues in production
     *
     * @return returns the block pos of the attached block entity
     */
    BlockPos getBlkPos();

    /**
     * Wrapper method for {@link BlockEntity#getLevel()} due to obfuscation
     * issues in production
     *
     * @return returns the level the entity is currently in
     */
    @Nullable Level getLvl();

    List<ServerPlayer> getListeners();

    CompoundTag getSyncData();

    void readSyncData(@NotNull CompoundTag tag);

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to the player specified.
     * @param player The player in which you want to send the data to.
     */
    default void sendToPlayer(ServerPlayer player) {
        if (getLvl() == null || getLvl().isClientSide) return;
        NetPacketHandler.CHANNEL.sendToPlayer(new SyncGuiPacket(this), player);
    }

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to all players tracking that chunk.
     */
    default void sendToPlayersTrackingChunk(){
        if (getLvl() == null || getLvl().isClientSide) return;
        NetPacketHandler.CHANNEL.sendToAllLoaded(new SyncGuiPacket(this), getLvl(), getBlkPos());
    }

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to all players within a range specified.
     * @param range the range in which to get players to send the data to.
     */
    default void sendToPlayersInRange(double range){
        if (getLvl() == null || getLvl().isClientSide) return;
        NetPacketHandler.CHANNEL.sendToPlayersInRange(new SyncGuiPacket(this), getLvl(), getBlkPos(), range);
    }

    /**
     * Sends {@link GUISyncedBlockEntity#getSyncData()} to all players listening to the block.
     * This will only work if {@link GUISyncedBlockEntity#addListeningPlayer(ServerPlayer)} has been called some where to add players listening.
     */
    default void sendToListeningPlayers() {
        if (getLvl() == null || getLvl().isClientSide) return;
        NetPacketHandler.CHANNEL.sendToPlayers(new SyncGuiPacket(this), getListeners());
    }

    default void addListeningPlayer(@NotNull ServerPlayer player) {
        getListeners().add(player);
    }

    default void removeListeningPlayer(@NotNull ServerPlayer player) {
        getListeners().remove(player);
    }
}
