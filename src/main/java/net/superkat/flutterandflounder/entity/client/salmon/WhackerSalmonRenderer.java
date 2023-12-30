package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.superkat.flutterandflounder.entity.custom.salmon.WhackerSalmonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WhackerSalmonRenderer extends GeoEntityRenderer<WhackerSalmonEntity> {
    public WhackerSalmonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WhackerSalmonModel());
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, WhackerSalmonEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        poseStack.scale(2f, 2f, 2f);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
