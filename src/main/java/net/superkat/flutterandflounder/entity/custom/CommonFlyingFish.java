package net.superkat.flutterandflounder.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.goals.FlyingGoals;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class CommonFlyingFish extends FlyingEntity implements GeoEntity, Monster {
    public Vec3d targetPosition = Vec3d.ZERO;
    public CommonFlyingFish(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlyingGoals.FlyingFishMoveControls(this, 2);
        this.lookControl = new FlyingGoals.FlyingFishLookControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FlyingGoals.FlyingAttackGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 15f, 1f));
        this.targetSelector.add(1, new FlyingGoals.FlyingFindTargetGoal(this));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {
            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }

            @Override
            public void tick() {
                super.tick();
            }
        };
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

}
