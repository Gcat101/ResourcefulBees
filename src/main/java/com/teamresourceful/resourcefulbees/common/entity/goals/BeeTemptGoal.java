package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class BeeTemptGoal extends Goal {
    private static final TargetingConditions ENTITY_PREDICATE = (TargetingConditions.forNonCombat()).range(10.0D).ignoreInvisibilityTesting().ignoreLineOfSight();
    protected final CustomBeeEntity beeEntity;
    private final double speed;
    protected Player closestPlayer;
    private int delayTemptCounter;


    public BeeTemptGoal(CustomBeeEntity beeEntity, double speedIn) {
        this.beeEntity = beeEntity;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(beeEntity.getNavigation() instanceof GroundPathNavigation) && !(beeEntity.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        }
        this.closestPlayer = this.beeEntity.level.getNearestPlayer(ENTITY_PREDICATE, this.beeEntity);
        return this.closestPlayer != null && (this.isTempting(this.closestPlayer.getMainHandItem()) || this.isTempting(this.closestPlayer.getOffhandItem()));
    }

    protected boolean isTempting(ItemStack stack) {
        return this.beeEntity.getBreedData().feedItems().contains(stack.getItem().builtInRegistryHolder());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void stop() {
        this.closestPlayer = null;
        this.beeEntity.getNavigation().stop();
        this.delayTemptCounter = 100;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick() {
        this.beeEntity.getLookControl().setLookAt(this.closestPlayer, (this.beeEntity.getMaxHeadYRot() + 20), this.beeEntity.getMaxHeadXRot());
        if (this.beeEntity.distanceToSqr(this.closestPlayer) < 6.25D) {
            this.beeEntity.getNavigation().stop();
        } else {
            this.beeEntity.getNavigation().moveTo(this.closestPlayer, this.speed);
        }

    }
}
