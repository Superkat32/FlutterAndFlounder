package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.superkat.flutterandflounder.entity.custom.cod.ClownCodEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ClownCodRenderer extends GeoEntityRenderer<ClownCodEntity> {
    public ClownCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ClownCodModel());
    }
//    @Override
//    public void render(ClownCodEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
//        poseStack.scale(2f, 2f, 2f);
//        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//    }


    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, ClownCodEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        poseStack.scale(2f, 2f, 2f);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
