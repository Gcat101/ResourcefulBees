package com.teamresourceful.resourcefulbees.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TickingBlock<E extends BlockEntity & InstanceBlockEntityTicker> extends RenderingBaseEntityBlock {

    private final RegistryObject<BlockEntityType<E>> blockEntity;

    public TickingBlock(RegistryObject<BlockEntityType<E>> blockEntity, Properties properties) {
        super(properties);
        this.blockEntity = blockEntity;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return blockEntity.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, blockEntity.get(), InstanceBlockEntityTicker.createTicker());
    }
}
