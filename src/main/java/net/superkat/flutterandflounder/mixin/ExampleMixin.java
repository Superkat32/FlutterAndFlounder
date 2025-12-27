package net.superkat.flutterandflounder.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.util.RenderUtil;

@Mixin(RenderUtil.class)
public class ExampleMixin {
	@WrapOperation(method = "prepMatrixForBone", at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/util/RenderUtil;translateAndRotateMatrixForBone(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/model/GeoBone;)V"))
    private static void flutterandflounder$tempGeckolibFix(PoseStack poseStack, GeoBone bone, Operation<Void> original) {
        original.call(poseStack, bone);
        if(bone.frameSnapshot != null) {
            bone.frameSnapshot.scale(poseStack);
        }
    }
}