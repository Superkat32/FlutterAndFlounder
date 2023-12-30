package net.superkat.flutterandflounder.entity.custom.cod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.custom.CommonBossFish;
import net.superkat.flutterandflounder.entity.goals.FleeFlounderFestGoal;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CodAutomobileEntity extends CommonBossFish {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.codautomobile.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public CodAutomobileEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends CodAutomobileEntity> PlayState animController(final AnimationState<E> event) {
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 2)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 2)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FleeFlounderFestGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 15f, 1f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
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
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

}
