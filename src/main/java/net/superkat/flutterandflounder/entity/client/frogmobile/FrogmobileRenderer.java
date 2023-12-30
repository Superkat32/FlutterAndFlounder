package net.superkat.flutterandflounder.entity.client.frogmobile;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.superkat.flutterandflounder.entity.custom.frogmobile.FrogmobileEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogmobileRenderer extends GeoEntityRenderer<FrogmobileEntity> {
    public FrogmobileRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FrogmobileModel());
    }

    @Override
    public void render(FrogmobileEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        poseStack.scale(2f, 2f, 2f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
