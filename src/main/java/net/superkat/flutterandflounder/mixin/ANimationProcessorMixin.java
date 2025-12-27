package net.superkat.flutterandflounder.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.state.AnimationPoint;

@Mixin(AnimationProcessor.class)
public class ANimationProcessorMixin {

    @ModifyReturnValue(method = "findAnimationPointValue", at = @At(value = "RETURN", ordinal = 1))
    private static float flutterandflounder$tempGeckoLibFixAlso(float original, @Local(argsOnly = true, name = "arg5") AnimationPoint.Transform transform) {
        if (transform == AnimationPoint.Transform.SCALE) return 1;
        return original;
    }

}
