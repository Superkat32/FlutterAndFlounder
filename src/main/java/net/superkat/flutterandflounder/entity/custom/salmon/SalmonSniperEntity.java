package net.superkat.flutterandflounder.entity.custom.salmon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.superkat.flutterandflounder.entity.custom.CommonBossFish;
import net.superkat.flutterandflounder.entity.goals.BossGoals;
import net.superkat.flutterandflounder.entity.goals.FleeFlounderFestGoal;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SalmonSniperEntity extends CommonBossFish implements RangedAttackMob {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.salmonsniper.idle");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.salmonsniper.attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public SalmonSniperEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends SalmonSniperEntity>PlayState animController(final AnimationState<E> event) {
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FleeFlounderFestGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1));
        this.targetSelector.add(1, new BossGoals.CrazyProjectileAttackGoal(this, 1, 200, 15f));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack salmonItem = new ItemStack(Items.SALMON);
        SalmonSniperProjectile projectile = new SalmonSniperProjectile(this.getWorld(), this, salmonItem);
        double x = target.getX() - this.getX();
        double y = target.getBodyY(0.333) - this.getY();
        double z = target.getZ() - this.getZ();
        double pos = Math.sqrt(x * x + z * z);
        projectile.setVelocity(x * 0.2f, y * 0.2f, z * 0.2f, 1.6f, (float) (14 / this.getWorld().getDifficulty().getId() * 8));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1f, 0.4f);
        this.getWorld().spawnEntity(projectile);
    }

    public static class SalmonSniperProjectile extends ArrowEntity {
        public SalmonSniperProjectile(EntityType<? extends ArrowEntity> entityType, World world) {
            super(entityType, world);
        }

        public SalmonSniperProjectile(World world, LivingEntity owner, ItemStack stack) {
            super(world, owner, stack);
        }

        @Override
        protected void onEntityHit(EntityHitResult entityHitResult) {
            if(entityHitResult.getEntity() instanceof PlayerEntity) {
                super.onEntityHit(entityHitResult);
            }
        }

        @Override
        protected void onBlockHit(BlockHitResult blockHitResult) {
            super.onBlockHit(blockHitResult); //plays sound and stuff
            this.discard();
        }
    }
}
