package net.superkat.flutterandflounder.entity.custom.frogmobile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AmbientStandGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.item.FlutterAndFlounderItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FrogmobileEntity extends AbstractHorseEntity implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.frogmobile.idle");
    protected static final RawAnimation MOVE_ANIM = RawAnimation.begin().thenLoop("animation.frogmobile.move");
    protected static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("animation.frogmobile.fly");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int flyTicks = 0;
    public int maxFlyTicks = 100;
    public boolean hasFlown = false;
    public FrogmobileEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends FrogmobileEntity> PlayState animController(final AnimationState<E> event) {
        if(this.hasNoGravity()) {
            return event.setAndContinue(FLY_ANIM);
        } else if(event.isMoving()) {
            return event.setAndContinue(MOVE_ANIM);
        }
        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new AmbientStandGoal(this));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(!this.hasPassengers()) {
            player.startRiding(this);
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean isTame() {
        return true;
    }

    protected Vec2f getControlledRotation(LivingEntity controllingPassenger) {
        return super.getControlledRotation(controllingPassenger);
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        if (this.isOnGround() && this.jumpStrength == 0.0F && this.isAngry() && !this.jumping) {
            return Vec3d.ZERO;
        } else {
            float f = controllingPlayer.sidewaysSpeed * 0.5F;
            float g = controllingPlayer.forwardSpeed * 1.25f;
            if (g <= 0.0F) {
                g *= 0.25F;
            }
            float y = 0;
            if(!this.isOnGround()) {
                if (g >= 0.3f) {
                    y  = controllingPlayer.getPitch() / -5;
                } else if (g >= 0.1f) {
                    y  = controllingPlayer.getPitch() / -15;
                }
            }
            return new Vec3d(f,  y, g);
        }
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        if(this.getVelocity().lengthSquared() > 0) {
            Vec2f vec2f = this.getControlledRotation(controllingPlayer);
            this.setRotation(vec2f.y, vec2f.x);
            this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.jumpStrength > 0.0F) {
                this.jump(this.jumpStrength, movementInput);
                this.jumpStrength = 0.0F;
            }
        }
        velocityDirty = true;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.hasPassengers()) {
            Entity var2 = this.getFirstPassenger();
            if (var2 instanceof PlayerEntity) {
                return (PlayerEntity)var2;
            }
        }

        return super.getControllingPassenger();
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if(passenger instanceof LivingEntity) {
            positionUpdater.accept(passenger, getX(), getY() + 0.2f, getZ());
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance > 1.0F) {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
        }

        int i = computeFallDamage(fallDistance, damageMultiplier);
        if (i <= 0) {
            return false;
        } else {
            //FIXME - Make it so that the "hasNoGravity()" boolean actually syncs between server and client
//            if(!hasNoGravity()) {
////                this.damage(damageSource, (float)i);
//                if (this.hasPassengers()) {
//                    for(Entity entity : this.getPassengersDeep()) {
//                        if(entity instanceof LivingEntity livingEntity) {
//                            if(i >= livingEntity.getHealth()) {
//                                i = (int) (livingEntity.getHealth() - 1);
//                            }
//                        }
////                        entity.damage(damageSource, (float)i);
//                    }
//                }
//            }

            this.playBlockFallSound();
            return true;
        }
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public double getJumpStrength() {
        return .5f;
    }

    @Override
    public void startJumping(int height) {
        super.startJumping(height);
        this.setInAir(true);
    }

    @Override
    public void setJumpStrength(int strength) {
        if (strength < 0) {
            strength = 0;
        } else {
            this.jumping = true;
            this.updateAnger();
        }

        if (strength >= 90) {
            this.jumpStrength = 1.0F;
        } else {
            this.jumpStrength = 0.4F + 0.4F * (float)strength / 90.0F;
        }
    }

    @Override
    protected void jump(float strength, Vec3d movementInput) {
        this.setNoGravity(!hasNoGravity());
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        this.dropStack(FlutterAndFlounderItems.FROGMOBILE_SPAWN_EGG.getDefaultStack());
    }

    @Override
    public void openInventory(PlayerEntity player) {
        //FIXME - Open player inventory instead
        super.openInventory(player);
    }

    @Override
    public ActionResult interactHorse(PlayerEntity player, ItemStack stack) {
        return ActionResult.PASS;
    }

    @Override
    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public void setAir(int air) {
        super.setAir(20); //always has air so it can't drown
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public EntityView method_48926() {
        return null;
    }
}
