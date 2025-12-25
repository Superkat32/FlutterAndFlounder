package net.superkat.flutterandflounder.entity.collectables;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PearlescentEgg extends PathfinderMob implements GeoEntity {
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation");

    protected final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public int age = 0;
    public int health = 5;

    public PearlescentEgg(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        if(this.firstTick && this.level().isClientSide()) {
            this.firstTick = false;
        } else {
            super.tick();
            this.move(MoverType.SELF, this.getDeltaMovement());

            this.age++;
            if(this.age >= 6000) {
                this.discard();
            }
        }
    }

    @Override
    public final boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f) {
        if (this.isInvulnerableToBase(damageSource)) {
            return false;
        } else {
            this.markHurt();
            this.health = (int)(this.health - f);
            if (this.health <= 0) {
                this.discard();
            }

            return true;
        }
    }

    @Override
    public boolean hurtClient(DamageSource damageSource) {
        return !this.isInvulnerableToBase(damageSource);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        this.health = valueInput.getShortOr("Health", (short)5);
        this.age = valueInput.getShortOr("Age", (short)0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        valueOutput.putShort("Health", (short)this.health);
        valueOutput.putShort("Age", (short)this.age);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>("test", 5, animatable -> animatable.setAndContinue(IDLE_ANIM))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
