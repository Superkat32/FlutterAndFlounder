package net.superkat.flutterandflounder.client.render.entity.collectables;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.superkat.flutterandflounder.FlutterAndFlounder;
import net.superkat.flutterandflounder.client.render.states.entity.collectables.PearlescentEggRenderState;
import net.superkat.flutterandflounder.entity.collectables.PearlescentEgg;

@Environment(EnvType.CLIENT)
public class OldPearlescentEggRenderer extends EntityRenderer<PearlescentEgg, PearlescentEggRenderState> {
    public static final Identifier PEARLESCENT_EGG_TEXTURE = Identifier.fromNamespaceAndPath(FlutterAndFlounder.MOD_ID, "textures/entity/pearlescent_egg.png");
    public static final RenderType PEARLESCENT_EGG_RENDER_TYPE = RenderTypes.itemEntityTranslucentCull(PEARLESCENT_EGG_TEXTURE);

    public OldPearlescentEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.75f;
    }

    @Override
    public void submit(PearlescentEggRenderState entityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        poseStack.translate(0f, 0.3f, 0f);
        poseStack.mulPose(cameraRenderState.orientation);

        float scale = 0.85f;
        poseStack.scale(scale, scale, scale);
        submitNodeCollector.submitCustomGeometry(poseStack, PEARLESCENT_EGG_RENDER_TYPE, (pose, vertexConsumer) -> {
            vertex(vertexConsumer, pose, -0.5f, -0.25f, 1, 1, 1, 0f, 1f, entityRenderState.lightCoords);
            vertex(vertexConsumer, pose, 0.5f, -0.25f, 1, 1, 1, 1f, 1f, entityRenderState.lightCoords);
            vertex(vertexConsumer, pose, 0.5f, 0.75f, 1, 1, 1, 1f, 0f, entityRenderState.lightCoords);
            vertex(vertexConsumer, pose, -0.5f, 0.75f, 1, 1, 1, 0f, 0f, entityRenderState.lightCoords);
        });

        poseStack.popPose();
        super.submit(entityRenderState, poseStack, submitNodeCollector, cameraRenderState);
    }

    private static void vertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x, float y, int r, int g, int b, float u, float v, int light) {
        vertexConsumer.addVertex(pose, x, y, 0.0F)
                .setColor(r, g, b, 1f)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    protected int getBlockLightLevel(PearlescentEgg entity, BlockPos blockPos) {
        return Mth.clamp(super.getBlockLightLevel(entity, blockPos) + 7, 0, 15);
    }

    @Override
    public PearlescentEggRenderState createRenderState() {
        return new PearlescentEggRenderState();
    }
}
