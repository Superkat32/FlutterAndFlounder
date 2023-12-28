package net.superkat.flutterandflounder.mixin.flounderfest;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.superkat.flutterandflounder.flounderfest.FlounderFestManager;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements FlounderFestServerWorld {

    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    public FlounderFestManager flounderFestManager = null;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/RaidManager;tick()V"))
    public void tickFlounderFestManager(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(flutterAndFlounder$getFlounderFestManager() != null) {
            flutterAndFlounder$getFlounderFestManager().tick();
        } else {
            flounderFestManager = this.getPersistentStateManager().getOrCreate(FlounderFestManager.getPersistentStateType((ServerWorld) (Object) this), "flounderfest");
        }
    }

    @Override
    public FlounderFestManager flutterAndFlounder$getFlounderFestManager() {
        return flounderFestManager;
    }
}
