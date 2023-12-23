package net.superkat.flutterandflounder.entity.custom.salmon;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.custom.CommonBossFish;
import net.superkat.flutterandflounder.entity.goals.BossGoals;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SalmonShipEntity extends CommonBossFish {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.salmonship.idle");
    protected static final RawAnimation WARNING_ANIM = RawAnimation.begin().thenPlay("animation.salmonship.warning");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlayAndHold("animation.salmonship.attack");
    protected static final RawAnimation FLOUNDER_ANIM = RawAnimation.begin().thenLoop("animation.salmonship.flounder");
    private boolean isWarning = false;
    //2 seconds
    public int warningTicks = 40;
    public boolean warningAnimInProgress = false;
    public boolean shouldAttack = false;
    public boolean playWarningAnim = false;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public SalmonShipEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new BossGoals.SalmonShipMoveControls(this, 4);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", 5, this::animController),
                new AnimationController<>(this, "warning", 5, this::warningController)

        );
    }

    protected PlayState animController(final AnimationState<SalmonShipEntity> event) {
        if(this.isAttacking()) {
            return event.setAndContinue(FLOUNDER_ANIM);
        }
        return event.setAndContinue(IDLE_ANIM);
    }

    protected PlayState warningController(final AnimationState<SalmonShipEntity> event) {
        if(this.isAttacking()) {
            return event.setAndContinue(WARNING_ANIM);
        }
        return event.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 25)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new BossGoals.SlamPlayerGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 15f, 1f));
        this.targetSelector.add(1, new BossGoals.BossFindTargetGoal(this));
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public void tick() {
        super.tick();
        if(isWarning) {
            warningTicks--;
            if(warningTicks <= 0) {
                isWarning = false;
                warningAnimInProgress = false;
                shouldAttack = true;
                warningTicks = 40;
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if(!this.getWorld().isClient) {
            updateFlounderFestQuota((ServerWorld) this.getWorld(), this.getBlockPos());
        }
        super.onDeath(damageSource);
    }

    public void setWarningTicks(int warningTicks) {
        this.warningTicks = warningTicks;
    }

    public void setIsWarning(boolean warning) {
        this.isWarning = warning;
        if(warning) {
            playWarningAnim = true;
        }
    }

    public boolean isWarning() {
        return this.isWarning;
    }

    public int getWarningTicks() {
        return warningTicks;
    }

    public boolean isShouldAttack() {
        return shouldAttack;
    }

    public void setShouldAttack(boolean shouldAttack) {
        this.shouldAttack = shouldAttack;
    }
}
