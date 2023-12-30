package net.superkat.flutterandflounder.entity.custom.cod;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
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
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

import java.util.Collections;
import java.util.List;

public class ChillCodEntity extends CommonBossFish implements RangedAttackMob {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.chillcod.idle");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("animation.chillcod.attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public ChillCodEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::animController));
    }

    protected<E extends ChillCodEntity> PlayState animController(final AnimationState<E> event) {
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FleeFlounderFestGoal(this));
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.0, 200, 10.0F));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 15f, 1f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        Box box = this.getBoundingBox().expand(50, 8, 50);
        List<PlayerEntity> players = this.getWorld().getPlayers(TargetPredicate.createNonAttackable().setBaseMaxDistance(45), this, box);
        float range = 8.5f;
        //divided by 50 to reduce particle count from roughly 5k to 150 particles
        float rangeTimesALotOfDigits = MathHelper.ceil((float) Math.PI * range * range) / 50f;
        Box freezingBox = this.getBoundingBox().expand(7, 5, 7);
        for (PlayerEntity player : players) {
            boolean inRange = freezingBox.contains(player.getPos());
            if(inRange) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2), this);
            }
            if(inRange) {
                player.setFrozenTicks(player.getFrozenTicks() + 3);
            } else {
                player.setFrozenTicks(Math.max(0, player.getFrozenTicks() - 10));
            }
        }
        super.tickMovement();
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient) {
            float range = 8.5f;
            //divided by 50 to reduce particle count from roughly 5k to 150 particles
            float rangeTimesALotOfDigits = MathHelper.ceil((float) Math.PI * range * range) / 50f;
            for (int i = 0; i < rangeTimesALotOfDigits; i++) {
                float h = this.random.nextFloat() * (float) (Math.PI * 2);
                float k = MathHelper.sqrt(this.random.nextFloat()) * range;
                double x = this.getX() + (double)(MathHelper.cos(h) * range);
                double y = this.getY();
                double z = this.getZ() + (double)(MathHelper.sin(h) * range);
                this.getWorld().addImportantParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.001, 0.001, 0.001);
            }
        }
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if(target != null && target.getVelocity() != null) {
            Vec3d vec3d = target.getVelocity();
            double d = target.getX() + vec3d.x - this.getX();
            double e = target.getEyeY() - 1.1F - this.getY();
            double f = target.getZ() + vec3d.z - this.getZ();
            double g = Math.sqrt(d * d + f * f);
            PotionEntity potionEntity = new PotionEntity(this.getWorld(), this);
            potionEntity.setItem(PotionUtil.setCustomPotionEffects(new ItemStack(Items.SPLASH_POTION), Collections.singleton(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 5))));
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
