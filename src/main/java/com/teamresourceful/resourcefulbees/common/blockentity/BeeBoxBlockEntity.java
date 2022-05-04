package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeeBoxBlockEntity extends BlockEntity {

    private List<CompoundTag> bees;
    private List<StringTag> displayNames;

    public BeeBoxBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntityTypes.BEE_BOX_ENTITY.get(), pWorldPosition, pBlockState);
    }

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_BEES, ModUtils.listTag(bees));
        tag.put(NBTConstants.NBT_DISPLAYNAMES, ModUtils.listTag(displayNames));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.bees = ModUtils.fromListTag(tag.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND), CompoundTag.class);
        this.displayNames = ModUtils.fromListTag(tag.getList(NBTConstants.NBT_DISPLAYNAMES, Tag.TAG_STRING), StringTag.class);
    }
    //endregion

    public void summonBees(Level level, BlockPos pos, Player player) {
        if (bees != null) {
            if (!(level instanceof ServerLevel serverLevel)) return;
            for (CompoundTag bee : bees) {
                EntityType.by(bee).ifPresent(entityType -> {
                    Entity entity = entityType.create(serverLevel);
                    if (entity != null) {
                        entity.load(bee);
                        entity.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                        serverLevel.addFreshEntity(entity);
                        if (entity instanceof Bee beeEntity) BeeJar.updateCapturedBee(beeEntity, player);
                    }
                });
            }
        }
    }

    public boolean hasBees() {
        return this.bees != null && !this.bees.isEmpty();
    }


}
