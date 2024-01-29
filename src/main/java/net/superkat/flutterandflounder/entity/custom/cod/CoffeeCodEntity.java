package net.superkat.flutterandflounder.entity.custom.cod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.custom.CommonBossFish;
import net.superkat.flutterandflounder.entity.goals.FleeFlounderFestGoal;
import net.superkat.flutterandflounder.item.FlutterAndFlounderItems;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collections;
import java.util.List;

public class CoffeeCodEntity extends CommonBossFish implements RangedAttackMob {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.coffeecod.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public CoffeeCodEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends CoffeeCodEntity> PlayState animController(final AnimationState<E> event) {
        if(!event.isMoving()) {
            return event.setAndContinue(IDLE_ANIM);
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeFlounderFestGoal(this));
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.0, 100, 10.0F));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 20f));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1, 20, false));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        Box coffeeBox = this.getBoundingBox().expand(7);
        List<CommonBossFish> nearbyBossFish = this.getWorld().getEntitiesByClass(CommonBossFish.class, coffeeBox, EntityPredicates.VALID_ENTITY);
        for (CommonBossFish bossFish : nearbyBossFish) {
            bossFish.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1));
        }
        super.tick();
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if(target != null && target.getVelocity() != null) {
            Box coffeeBox = this.getBoundingBox().expand(7);
            List<CommonBossFish> nearbyBossFish = this.getWorld().getEntitiesByClass(CommonBossFish.class, coffeeBox, EntityPredicates.VALID_ENTITY);
            Vec3d vec3d = target.getVelocity();
            CommonBossFish randomNearbyBossFish = nearbyBossFish.get(this.random.nextInt(nearbyBossFish.size()));
            double d = randomNearbyBossFish.getX() + vec3d.x - this.getX();
            double e = randomNearbyBossFish.getEyeY() - 1.1F - this.getY();
            double f = randomNearbyBossFish.getZ() + vec3d.z - this.getZ();
            double g = Math.sqrt(d * d + f * f);
            PotionEntity potionEntity = new PotionEntity(this.getWorld(), this);
            potionEntity.setItem(PotionUtil.setCustomPotionEffects(new ItemStack(FlutterAndFlounderItems.FLOUNDERFEST_COFFEE), Collections.singleton(new StatusEffectInstance(StatusEffects.SPEED, 40, 2))));
            potionEntity.setPitch(potionEntity.getPitch() - -20.0F);
            potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.getWorld()
                        .playSound(
                                null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
                        );
            }

            this.getWorld().spawnEntity(potionEntity);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_COD_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_COD_HURT;
    }
}
