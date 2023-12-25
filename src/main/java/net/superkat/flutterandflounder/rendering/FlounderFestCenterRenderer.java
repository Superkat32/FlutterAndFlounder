package net.superkat.flutterandflounder.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class FlounderFestCenterRenderer {
    public BlockPos renderPos;
    public FlounderFestCenterRenderer(BlockPos renderPos) {
        this.renderPos = renderPos;
    }

    public void renderFlounderFestCenter(WorldRenderContext context) {
        Camera camera = context.camera();

        Vec3d targetPos = renderPos.toCenterPos();
        Vec3d pos = targetPos.subtract(camera.getPos());

        MatrixStack stack = new MatrixStack();
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180f));
        stack.translate(pos.x, pos.y, pos.z);

        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(matrix4f, 0, 3, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 1, 0, 0).color(1f, 1f, 1f, 1f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 1, 3, 0).color(1f, 1f, 1f, 1f).texture(1f, 0f).next();

        buffer.vertex(matrix4f, 0, 3, 1).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 0, 0, 1).color(1f, 1f, 1f, 1f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 1, 0, 1).color(1f, 1f, 1f, 1f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 1, 3, 1).color(1f, 1f, 1f, 1f).texture(1f, 0f).next();

        buffer.vertex(matrix4f, 0, 3, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 0, 0, 1).color(1f, 1f, 1f, 1f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 0, 3, 1).color(1f, 1f, 1f, 1f).texture(1f, 0f).next();

        buffer.vertex(matrix4f, 1, 3, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 1, 0, 0).color(1f, 1f, 1f, 1f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 1, 0, 1).color(1f, 1f, 1f, 1f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 1, 3, 1).color(1f, 1f, 1f, 1f).texture(1f, 0f).next();

        //outline
        matrix4f.translate(-.25f, 0f, -.25f); //half of the scaled amount
        matrix4f.scale(1.5f, 1f, 1.5f);
        buffer.vertex(matrix4f, 0, 3, 0).color(1f, 1f, 1f, 0.3f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 0, 0, 0).color(1f, 1f, 1f, 0.3f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 1, 0, 0).color(1f, 1f, 1f, 0.3f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 1, 3, 0).color(1f, 1f, 1f, 0.3f).texture(1f, 0f).next();

        buffer.vertex(matrix4f, 0, 3, 1).color(1f, 1f, 1f, 0.3f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 0, 0, 1).color(1f, 1f, 1f, 0.3f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 1, 0, 1).color(1f, 1f, 1f, 0.3f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 1, 3, 1).color(1f, 1f, 1f, 0.3f).texture(1f, 0f).next();

        buffer.vertex(matrix4f, 0, 3, 0).color(1f, 1f, 1f, 0.3f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 0, 0, 0).color(1f, 1f, 1f, 0.3f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 0, 0, 1).color(1f, 1f, 1f, 0.3f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 0, 3, 1).color(1f, 1f, 1f, 0.3f).texture(1f, 0f).next();

        buffer.vertex(matrix4f, 1, 3, 0).color(1f, 1f, 1f, 0.3f).texture(0f, 0f).next();
        buffer.vertex(matrix4f, 1, 0, 0).color(1f, 1f, 1f, 0.3f).texture(0f, 1f).next();
        buffer.vertex(matrix4f, 1, 0, 1).color(1f, 1f, 1f, 0.3f).texture(1f, 1f).next();
        buffer.vertex(matrix4f, 1, 3, 1).color(1f, 1f, 1f, 0.3f).texture(1f, 0f).next();

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.setShaderTexture(0, new Identifier("textures/entity/beacon_beam.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableBlend();

        tessellator.draw();

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

}
