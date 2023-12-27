package net.superkat.flutterandflounder.entity.custom.cod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.goals.FlyingGoals;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoonCodEntity extends HostileEntity implements GeoEntity, Ownable {
    protected static final RawAnimation MOVE_ANIM = RawAnimation.begin().thenLoop("animation.goon.move");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlayAndHold("animation.goon.attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public boolean hasAttemptedAttack = false;
    public int ticksSinceAttack = 0;
    public int ticksAlive = 0;
    @Nullable
    public ClownCodEntity owner = null;
    public GoonCodEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlyingGoals.GoonMoveControls(this, 5);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends GoonCodEntity> PlayState animController(final AnimationState<E> event) {
        if(hasAttemptedAttack) {
            ticksSinceAttack++;
            if(ticksSinceAttack >= 20) {
                return PlayState.STOP;
            }
            return event.setAndContinue(ATTACK_ANIM);
        }
        else if(this.isAttacking()) {
            hasAttemptedAttack = true;
            return event.setAndContinue(ATTACK_ANIM);
        } else {
            return event.setAndContinue(MOVE_ANIM);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FlyingGoals.GoonGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 1)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        ticksAlive++;
        if(ticksAlive % 2 == 0) {
            this.getWorld().addImportantParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0.001, -0.5, 0.001);
        }
        if (ticksAlive % 20 == 0) {
            this.getWorld().playSoundFromEntity(this, SoundEvents.ENTITY_VEX_CHARGE, SoundCategory.HOSTILE, 1f, 1.2f);
        }
        if(ticksAlive >= 200) {
            this.kill();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        DamageSource trueSource = new DamageSource(source.getTypeRegistryEntry(), source.getSource(), getOwner());
        return super.damage(trueSource, amount);
    }

    public void setOwner(@Nullable ClownCodEntity owner) {
        this.owner = owner;
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return owner;
    }
}
