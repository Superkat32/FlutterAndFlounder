package net.superkat.flutterandflounder.entity.goals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonShipEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class BossGoals {
    public static class BossFindTargetGoal extends Goal {
        private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
        private int delay = toGoalTicks(20);
        protected final HostileEntity entity;
        public BossFindTargetGoal(HostileEntity entity) {
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

    public static class SlamPlayerGoal extends Goal {
        private final SalmonShipEntity entity;
        private Path path;
        private int cooldown;
        private int maxCooldown = 100;
        private int attackIntervalTicks;
        private int ticksSinceLastAttack;
        private boolean stationary = false;
        private boolean warning = false;

        public SlamPlayerGoal(SalmonShipEntity entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean shouldContinue() {
            ServerPlayerEntity target = (ServerPlayerEntity) entity.getTarget();
            return target != null;
        }

        @Override
        public void start() {
            ServerPlayerEntity target = (ServerPlayerEntity) entity.getTarget();
//            entity.getNavigation().startMovingAlong(path, entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            if(target != null) {
                Vec3d vec3d = target.getEyePos();
                entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                cooldown = maxCooldown;
            }
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        public boolean canStart() {
            ServerPlayerEntity target = (ServerPlayerEntity) entity.getTarget();
            return target != null;
        }

        @Override
        public void tick() {
            ServerPlayerEntity target = (ServerPlayerEntity) entity.getTarget();
            if(target != null) {
                if(entity.getBoundingBox().intersects(target.getBoundingBox())) {
                    entity.setAttacking(true);
                    entity.tryAttack(target);
                } else {
                    double distance = entity.squaredDistanceTo(target);
                    if(distance < entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)) {
                        Vec3d vec3d = target.getEyePos();
                        entity.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
                    }
                }
            }
        }
    }

    public static class SalmonShipMoveControls extends MoveControl {
        private final SalmonShipEntity salmonShipEntity;
        private int stoppingDistanceFromPlayer;
        private int cooldown;
        private int maxCooldown = 100;
        private int attackIntervalTicks;
        private int ticksSinceLastAttack;
        private boolean stationary = false;
        private boolean canReturnUp = false;
        public SalmonShipMoveControls(MobEntity entity, int stoppingDistanceFromPlayer) {
            super(entity);
            if(entity instanceof SalmonShipEntity) {
                this.salmonShipEntity = (SalmonShipEntity) entity;
            } else {
                this.salmonShipEntity = null;
            }
            this.stoppingDistanceFromPlayer = stoppingDistanceFromPlayer;
        }

        @Override
        public void tick() {
            double x = targetX - entity.getX();
            double y = targetY - entity.getY() + 4;
            double z = targetZ - entity.getZ();
            if(this.state == State.MOVE_TO && !stationary) {
                entity.setAttacking(false);
                Vec3d vec3d = new Vec3d(x, y, z);
                double d = vec3d.length();
                entity.setNoGravity(true);
                ServerPlayerEntity target = (ServerPlayerEntity) entity.getTarget();
                if(target != null) {
                    double xDistance = Math.abs(Math.abs(entity.getX()) - Math.abs(target.getX()));
                    double zDistance = Math.abs(Math.abs(entity.getZ()) - Math.abs(target.getZ()));
                    Vec3d vec3d1 = vec3d.multiply(speed * 0.05 / 3);
                    entity.setVelocity(entity.getVelocity().add(vec3d1.getX(), vec3d.multiply(speed * 0.05 / 5).getY(), vec3d1.getZ()));
                    if(xDistance < 10 && zDistance < 10) {
                        stationary = true;
                    }

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
//                    }
                }
            } else {
                ServerPlayerEntity target = (ServerPlayerEntity) entity.getTarget();
                if(target != null) {
                    double xDistance = Math.abs(Math.abs(entity.getX()) - Math.abs(target.getX()));
                    double zDistance = Math.abs(Math.abs(entity.getZ()) - Math.abs(target.getZ()));
                    if(stationary) {
                        if (xDistance >= 7 || zDistance >= 7 || canReturnUp) {
                            stationary = false;
                            canReturnUp = false;
                        }
                        if(salmonShipEntity != null && salmonShipEntity.shouldAttack) {
                            //doesn't matter if a player is beneath the salmon ship or not
                            entity.setNoGravity(false);
                            salmonShipEntity.setShouldAttack(false);
                            cooldown = maxCooldown;
                        } else {
                            boolean beneath = xDistance <= 3.5 && zDistance <= 3.5;
                            if(beneath) {
                                Vec3d vec3d = new Vec3d(x, y, z);
                                Vec3d vec3d1 = vec3d.multiply(speed * 0.05 / 3);
                                entity.setVelocity(entity.getVelocity().add(vec3d1.getX(), vec3d.multiply(speed * 0.05 / 5).getY(), vec3d1.getZ()));
                                if (salmonShipEntity != null) {
                                    salmonShipEntity.setIsWarning(true);
                                    entity.setAttacking(true);
                                }
                            }
                        }

                        cooldown--;
                        if(cooldown <= 0) {
                            canReturnUp = true;
                        }
                    }
                }
            }
        }
    }

    public static class CrazyProjectileAttackGoal extends Goal {
        private final MobEntity mob;
        private final RangedAttackMob owner;
        @Nullable
        private LivingEntity target;
        private int updateCountdownTicks = -1;
        private final double mobSpeed;
        private int seenTargetTicks;
        private final int minIntervalTicks;
        private final int maxIntervalTicks;
        private final float maxShootRange;
        private final float squaredMaxShootRange;
        private int ticksPerShot;
        private int shootUpdateTicks = 0;

        public CrazyProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int interval, float maxShootRange) {
            if (!(mob instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.owner = mob;
                this.mob = (MobEntity)mob;
                this.mobSpeed = mobSpeed;
                this.minIntervalTicks = interval;
                this.maxIntervalTicks = interval;
                this.ticksPerShot = interval;
                this.maxShootRange = maxShootRange;
                this.squaredMaxShootRange = maxShootRange * maxShootRange;
                this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
            }
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null && livingEntity.isAlive()) {
                this.target = livingEntity;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart() || this.target.isAlive() && !this.mob.getNavigation().isIdle();
        }

        @Override
        public void stop() {
            this.target = null;
            this.seenTargetTicks = 0;
            this.updateCountdownTicks = -1;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean bl = this.mob.getVisibilityCache().canSee(this.target);
            if (bl) {
                ++this.seenTargetTicks;
                shootUpdateTicks++;
            } else {
                this.seenTargetTicks = 0;
            }

            if (!(d > (double)this.squaredMaxShootRange) && this.seenTargetTicks >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
            }

            this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
            if(shootUpdateTicks >= 50) { //terrible i know but I'm on a time limit
                ticksPerShot = 30;
                if(shootUpdateTicks >= 80) {
                    ticksPerShot = 20;
                    if(shootUpdateTicks >= 100) {
                        ticksPerShot = 10;
                        if(shootUpdateTicks >= 120) {
                            ticksPerShot = 5;
                            if(shootUpdateTicks >= 130) {
                                ticksPerShot = 2;
                                if(shootUpdateTicks >= 170) {
                                    ticksPerShot = minIntervalTicks;
                                    shootUpdateTicks = 0;
                                }
                            }
                        }
                    }
                }
            }
            if(shootUpdateTicks % ticksPerShot == 0) {
                float f = (float)Math.sqrt(d) / this.maxShootRange;
                float g = MathHelper.clamp(f, 0.1F, 1.0F);
                this.owner.shootAt(this.target, g);
            }
        }
    }

}