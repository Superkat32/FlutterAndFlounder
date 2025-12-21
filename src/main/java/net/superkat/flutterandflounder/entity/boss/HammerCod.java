package net.superkat.flutterandflounder.entity.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HammerCod extends PathfinderMob implements GeoEntity {
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.hammercod.idle");
    public static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.hammercod.attack");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public HammerCod(EntityType<? extends @NotNull PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>("idle", 5, animationTest -> animationTest.setAndContinue(IDLE_ANIM))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
