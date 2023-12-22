package net.superkat.flutterandflounder.mixin.flounderfest;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.superkat.flutterandflounder.flounderfest.FlounderFestManager;
import net.superkat.flutterandflounder.rendering.FlutterAndFlounderRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    public void flutterAndFlounder$changeSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if(FlutterAndFlounderRendering.shouldChangeSky) {
            Vec3d skyColor = cir.getReturnValue();
            Vec3d updatedSkyColor = FlounderFestManager.getFlounderFestSkyColor(skyColor);
            cir.setReturnValue(updatedSkyColor);
        }
    }

}
