package net.superkat.flutterandflounder.entity.goals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.superkat.flutterandflounder.entity.custom.CommonFlyingFish;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class FlyingGoals {

    public static abstract class AbstractFlyingMovementGoal extends Goal {
        protected final CommonFlyingFish entity;

        public AbstractFlyingMovementGoal(CommonFlyingFish entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected boolean isNearTarget() {
            return entity.targetPosition.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) < entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        }
    }

    public static class FlyingFishMoveControls extends MoveControl {
        private final int maxPitchChange;

        public FlyingFishMoveControls(MobEntity entity, int maxPitchChange) {
            super(entity);
            this.maxPitchChange = maxPitchChange;
        }

        @Override
        public void tick() {
            if (this.state == MoveControl.State.MOVE_TO) {
                double x = targetX - entity.getX();
                double y = targetY - entity.getY();
                double z = targetZ - entity.getZ();
                Vec3d vec3d = new Vec3d(x, y, z);
                double d = vec3d.length();
                if (d < entity.getBoundingBox().getAverageSideLength()) {
                    this.state = MoveControl.State.WAIT;
                    entity.setVelocity(entity.getVelocity().multiply(0.5));
                } else {
                    entity.setVelocity(entity.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                    if (entity.getTarget() == null) {
                        Vec3d vec3d2 = entity.getVelocity();
                        entity.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * (180.0F / (float)Math.PI));
                        entity.bodyYaw = entity.getYaw();
                    } else {
                        double e = entity.getTarget().getX() - entity.getX();
                        double f = entity.getTarget().getZ() - entity.getZ();
                        entity.setYaw(-((float)MathHelper.atan2(e, f)) * (180.0F / (float)Math.PI));
                        entity.bodyYaw = entity.getYaw();
                    }

//                    float i = (float)(this.speed * 5f);
//                    double j = Math.sqrt(x * x + z * z);
//                    if(Math.abs(y) > 1.0E-5F || Math.abs(j) > 1.0E-5F) {
//                        float k = (float)(-(MathHelper.atan2(y, j) * 180.0F / (float)Math.PI));
//                        this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), k, (float)this.maxPitchChange));
//                        this.entity.setUpwardSpeed(y > 0.0 ? i : -i);
//                    }
                }
            }
        }
    }

    public static class FlyingFishLookControl extends LookControl {

        public FlyingFishLookControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
        }
    }

    public static class FlyingAttackGoal extends Goal {
        protected final CommonFlyingFish entity;
        public FlyingAttackGoal(CommonFlyingFish entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = entity.getTarget();
            return livingEntity != null && livingEntity.isAlive();
        }

        @Override
        public boolean shouldContinue() {
            if(entity.getTarget() != null && entity.getTarget().isAttackable()) {
                if(entity.getTarget() instanceof PlayerEntity player) {
                    if(player.isCreative()) return false;
                }
                return super.shouldContinue();
            } else {
                entity.setTarget(null);
                return false;
            }
        }

        @Override
        public void start() {
            LivingEntity livingEntity = entity.getTarget();
            if(livingEntity != null) {
                Vec3d vec3d = livingEntity.getEyePos();
                entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED));
            }
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = entity.getTarget();
            if (livingEntity != null) {
                if (entity.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                    entity.tryAttack(livingEntity);
                } else {
                    double d = entity.squaredDistanceTo(livingEntity);
                    if (d < entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)) {
                        Vec3d vec3d = livingEntity.getEyePos();
                        entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED));
                    }
                }
            }
        }
    }

    public static class FlyingFindTargetGoal extends Goal {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
        private int delay = toGoalTicks(20);
        protected final CommonFlyingFish entity;
        public FlyingFindTargetGoal(CommonFlyingFish entity) {
            this.entity = entity;
        }

        @Override
        public boolean canStart() {
            if (this.delay > 0) {
                --this.delay;
                return false;
            } else {
                this.delay = toGoalTicks(60);
                List<PlayerEntity> list = entity.getWorld()
                        .getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, entity, entity.getBoundingBox().expand(16.0, 64.0, 16.0));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::getY).reversed());

                    for(PlayerEntity playerEntity : list) {
                        if (entity.isTarget(playerEntity, TargetPredicate.DEFAULT)) {
                            entity.setTarget(playerEntity);
                            return true;
                        }
                    }
                }

                entity.setTarget(null);
                return false;
            }
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = entity.getTarget();
            return livingEntity != null ? entity.isTarget(livingEntity, TargetPredicate.DEFAULT) : false;
        }
    }



}
