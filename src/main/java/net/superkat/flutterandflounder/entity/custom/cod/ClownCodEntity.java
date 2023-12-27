package net.superkat.flutterandflounder.entity.custom.cod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.custom.CommonBossFish;
import net.superkat.flutterandflounder.entity.goals.FleeFlounderFestGoal;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ClownCodEntity extends CommonBossFish {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.clowncod.idle");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.clowncod.attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int ticksUntilGoons = 300;
    public ClownCodEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends ClownCodEntity> PlayState animController(final AnimationState<E> event) {
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
//        this.goalSelector.add(1, new MeleeAttackGoal(this, 1, true));
        this.goalSelector.add(1, new FleeFlounderFestGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        ticksUntilGoons--;
        if(ticksUntilGoons <= 0) {
            ticksUntilGoons = 400;
            int goons = 7;
            for(int i = 0; i < goons; i++) {
                summonGoon();
            }
        }
    }

    public void summonGoon() {
        GoonCodEntity goon = FlutterAndFlounderEntities.GOON.create(this.getWorld());
        if(goon != null) {
            goon.setPos(this.getX(), this.getY(), this.getZ());
            if(this.getTarget() != null) {
                goon.setTarget(this.getTarget());
                double velX = this.getTarget().getX() - this.getX();
                double velY = this.getTarget().getY() - this.getY();
                double velZ = this.getTarget().getZ() - this.getZ();
                goon.setVelocity(velX, velY, velZ);
                goon.setOwner(this);
                this.getWorld().spawnEntity(goon);
            }
        }
    }
}
