package net.superkat.flutterandflounder.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class FlutterAndFlounderRendering {
    public static boolean shouldChangeSky = false;
    public static double skyChangeMultiplier = 0;
    @Nullable
    public static FlounderFestHud flounderFestHud = null;

    public FlutterAndFlounderRendering() {
    }

    public static void registerHudEvents() {
        HudRenderCallback.EVENT.register(FlutterAndFlounderRendering::renderFlounderFestHuds);
    }

    public static void renderFlounderFestHuds(DrawContext context, float tickDelta) {
        if(flounderFestHud != null) {
            shouldChangeSky = true;
            if(skyChangeMultiplier < 1) {
                skyChangeMultiplier += 0.005;
            }
            renderHud(context, tickDelta, flounderFestHud);
        } else {
            if(skyChangeMultiplier > 0) {
                skyChangeMultiplier -= 0.005;
            } else {
                shouldChangeSky = false;
            }
        }
    }

    private static void renderHud(DrawContext context, float delta, FlounderFestHud hud) {
        MinecraftClient client = MinecraftClient.getInstance();

        int windowWidth = context.getScaledWindowWidth();

        //"FlounderFest" title at the top of the screen
        Text title = Text.translatable("flutterandflounder.flounderfest.title");
        int titleTextWidth = client.textRenderer.getWidth(title);
        int titleX = windowWidth / 2 - titleTextWidth / 2;
        int titleY = 3;
        context.drawTextWithShadow(client.textRenderer, title, titleX, titleY, 16777215);

        //"Wave <current wave>" number at the top middle left of the screen
        Text wave = Text.translatable("flutterandflounder.flounderfest.wave").append(" " + hud.waveNum);
        int waveX = windowWidth / 2 - titleTextWidth / 2 - titleTextWidth / 4;
        int waveY = 6 + titleY * 2;
        context.drawTextWithShadow(client.textRenderer, wave, waveX, waveY, new Color(81, 169, 255).getRGB());

        //"Quota" text at the top middle right of the screen
        Text quotaTitle = Text.translatable("flutterandflounder.flounderfest.quota");
        int quotaTitleX = windowWidth / 2 + titleTextWidth / 4;
        int quotaTitleY = 6 + titleY * 2;
        context.drawTextWithShadow(client.textRenderer, quotaTitle, quotaTitleX, quotaTitleY, new Color(81, 169, 255).getRGB());

        //color used to indicate danger if quota isn't met yet
        Color timeColor = new Color(175, 238, 255);
        Color quotaColor = new Color(175, 238, 255);
        if(hud.secondsLeft <= 10) {
            if(hud.quotaProgress < hud.maxQuota) {
                quotaColor = new Color(232, 65, 61);
                timeColor = new Color(232, 65, 61);
            }
        } else if (hud.secondsLeft <= 30) {
            if(hud.quotaProgress < hud.maxQuota) {
                timeColor = new Color(232, 65, 61);
            }
        }

        //"<seconds remaining>" text at the middle left of the screen(beneath wave)
        Text time = Text.literal(String.valueOf(hud.secondsLeft));
        int timeTextWidth = client.textRenderer.getWidth(time);
        int timeX = windowWidth / 2 - titleTextWidth / 2 - timeTextWidth / 2;
        int timeY = 10 + titleY * 4;
        context.drawTextWithShadow(client.textRenderer, time, timeX, timeY, timeColor.getRGB());

        //"<quota progress>" text at the middle right of the screen(beneath quota title)
        Text quota = Text.literal(hud.quotaProgress + "/" + hud.maxQuota);
        int quotaTextWidth = client.textRenderer.getWidth(quota);
        int quotaChar = quotaTextWidth / 6;
        int quotaX = windowWidth / 2 + titleTextWidth / 4 + quotaTextWidth / 8;
        if(quotaChar == 3) { //done because double-digit max quotas(4 characters) is more likely
            quotaX = windowWidth / 2 + titleTextWidth / 4 + quotaTextWidth / 3;
        }

        context.drawTextWithShadow(client.textRenderer, quota, quotaX, timeY, quotaColor.getRGB());
    }

    public void setFlounderFestHud(@Nullable FlounderFestHud flounderFestHud) {
        FlutterAndFlounderRendering.flounderFestHud = flounderFestHud;
        shouldChangeSky = flounderFestHud != null;
    }

    @Nullable
    public static FlounderFestHud getCurrentHud() {
        return flounderFestHud;
    }

}
