package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.superkat.flutterandflounder.entity.custom.cod.ChillCodEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChillCodRenderer extends GeoEntityRenderer<ChillCodEntity> {
    public ChillCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChillCodModel());
    }

//    @Override
//    public void render(ChillCodEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
//        poseStack.scale(1.2f, 1.2f, 1.2f);
//        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//    }


    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, ChillCodEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        poseStack.scale(1.2f, 1.2f,1.2f);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
