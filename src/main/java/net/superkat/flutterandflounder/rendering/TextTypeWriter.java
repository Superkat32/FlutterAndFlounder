package net.superkat.flutterandflounder.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class TextTypeWriter {
    public Text text;
    public int x;
    public int y;
    public int typeDelay;
    public int renderTime;
    public Color textColor;
    public boolean shouldCenter = false;
    public boolean fadeIn = false;
    public int fadeInTicks = 0;
    public boolean fadeOut = false;
    public int fadeOutTicks = 0;
    public boolean finished = false;

    private int currentDelay = 0;
    private int ticks;
    private int index = 0;
    private Text renderedText;
    private float opacity = 1f;

    /**
     * Renders Minecraft Text with a simple typewriter special effect.
     *
     * @param text The Minecraft Text which gets rendered
     * @param x The text's x pos
     * @param y The text's y pos
     * @param typeDelay The delay in ticks between each type
     * @param renderTime The time in ticks before the TextTypeWriter stops rendering text
     * @param textColor The color of the rendered text
     */
    public TextTypeWriter(Text text, int x, int y, int typeDelay, int renderTime, Color textColor) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.typeDelay = typeDelay;
        this.renderTime = renderTime;
        this.textColor = textColor;
    }

    public void setFadeIn(int fadeInTicks) {
        this.fadeInTicks = Math.abs(fadeInTicks);
        this.fadeIn = fadeInTicks != 0;
        if(this.fadeIn) {
            this.opacity = 0f;
        }
    }

    public void setFadeOut(int fadeOutTicks) {
        this.fadeOutTicks = Math.abs(fadeOutTicks);
        this.fadeOut = fadeOutTicks != 0;
    }

    /**
     * Renders text on screen based on the parameters given in the constructor.
     * Should be called every tick.
     */
    public void writeText(DrawContext context, MatrixStack matrixStack) {
        if(ticks <= renderTime) {
            matrixStack.push();
            ticks++;

            //typewriter effect
            if(index < text.getString().length()) {
                currentDelay++;
                if(currentDelay >= typeDelay) {
                    currentDelay = 0;
                    index++;
                }
            }

            //fade in
            if(fadeIn) {
                if(ticks <= fadeInTicks) {
                    if(ticks == 1 && opacity == 1f) {
                        opacity = 0f; //safety check
                    }
                    if(opacity != 1f) {
                        opacity = MathHelper.clamp(opacity + 1f / fadeInTicks, 0f, 1f);
                    }
                }
            }

            //fade out
            if(fadeOut) {
                if(ticks >= renderTime - fadeOutTicks) {
                    if(opacity != 0f) {
                        opacity = MathHelper.clamp(opacity - 1f / fadeOutTicks, 0f, 1f);
                    }
                }
            }

            renderedText = Text.of(text.getString().substring(0, index));

            RenderSystem.enableBlend();
            context.setShaderColor(1f, 1f, 1f, opacity);
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, renderedText, x, y, textColor.getRGB());
            context.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            matrixStack.pop();
        } else {
            finished = true;
        }
    }
}
