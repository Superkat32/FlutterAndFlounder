package net.superkat.flutterandflounder.mixin.flounderfest;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.GameRenderer;
import net.superkat.flutterandflounder.rendering.FlutterAndFlounderRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    //I love mixin extras
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;shouldDarkenSky()Z"))
    public boolean flutterAndFlounder$darkenSkyWhenInFlounderFest(boolean original) {
        return original || FlutterAndFlounderRendering.shouldChangeSky;
    }
}
