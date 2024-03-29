package net.superkat.flutterandflounder.entity.custom.cod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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

import java.util.List;

public class HammerCodEntity extends CommonBossFish {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.hammercod.idle");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.hammercod.attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public HammerCodEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController)
                        .setParticleKeyframeHandler(state -> {
                            BlockPos pos = state.getAnimatable().getBlockPos();
                            BlockPos particlePos = pos.offset(state.getAnimatable().getHorizontalFacing(), 2);
                            this.getWorld().addBlockBreakParticles(particlePos, state.getAnimatable().getSteppingBlockState());
                        }).setSoundKeyframeHandler(state -> {
                            Box box = state.getAnimatable().getBoundingBox().expand(15.0, 8.0, 15.0);
                            List<PlayerEntity> players = this.getWorld().getPlayers(TargetPredicate.createNonAttackable().setBaseMaxDistance(15), state.getAnimatable(), box);

                            for(PlayerEntity player : players) {
                                player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 1, 0.7f);
                            }
                        })
        );
    }

    protected<E extends HammerCodEntity> PlayState animController(final AnimationState<E> event) {
        if(this.isAttacking()) {
            return event.setAndContinue(ATTACK_ANIM);
        } else {
            return event.setAndContinue(IDLE_ANIM);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 10)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 2)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FleeFlounderFestGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 15f, 0.5f));
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
        return SoundEvents.ENTITY_COD_DEATH;
    }

}
