package net.superkat.flutterandflounder.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.falseresync.libhudcompat.LibHudCompat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static net.superkat.flutterandflounder.FlutterAndFlounderMain.MOD_ID;

public class FlutterAndFlounderRendering {
    public static final Identifier transparent1 = new Identifier(MOD_ID, "textures/numbers/1.png");
    public static final Identifier transparent2 = new Identifier(MOD_ID, "textures/numbers/2.png");
    public static final Identifier transparent3 = new Identifier(MOD_ID, "textures/numbers/3.png");
    public static final Identifier transparent4 = new Identifier(MOD_ID, "textures/numbers/4.png");
    public static final Identifier transparent5 = new Identifier(MOD_ID, "textures/numbers/5.png");
    public static final Identifier transparent6 = new Identifier(MOD_ID, "textures/numbers/6.png");
    public static final Identifier transparent7 = new Identifier(MOD_ID, "textures/numbers/7.png");
    public static final Identifier transparent8 = new Identifier(MOD_ID, "textures/numbers/8.png");
    public static final Identifier transparent9 = new Identifier(MOD_ID, "textures/numbers/9.png");
    public static final Identifier transparent10 = new Identifier(MOD_ID, "textures/numbers/10.png");
    public static boolean shouldChangeSky = false;
    public static double skyChangeMultiplier = 0;
    public static boolean shouldPlayQuotaUpdateAnimation = false;
    public static float quotaUpdateAnimOpacity = 0;
    public static int ticksSinceQuotaUpdateAnim = 0;
    public static boolean shouldPlayBossAnim = false;
    public static int bossAnimX = 0;
    public static int ticksSinceBossAnim = 0;
    @Nullable
    public static FlounderFestHud flounderFestHud = null;
    public static TextTypeWriter textTypeWriter = null;
    public static boolean renewTextTypeWriter = true;

    @Nullable
    public static FlounderFestCenterRenderer flounderFestCenterRenderer = null;

    public static void registerHudEvents() {
        HudRenderCallback.EVENT.register(FlutterAndFlounderRendering::renderFlounderFestHuds);

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> { //prevents the hud from showing in other worlds
            FlutterAndFlounderRendering.flounderFestHud = null;
            FlutterAndFlounderRendering.flounderFestCenterRenderer = null;
        }));
    }

    public static void registerWorldEvents() {
        WorldRenderEvents.END.register(FlutterAndFlounderRendering::renderFlounderFestCenter);
    }

    public static void renderFlounderFestCenter(WorldRenderContext worldRenderContext) {
        if(flounderFestCenterRenderer != null) {
            flounderFestCenterRenderer.renderFlounderFestCenter(worldRenderContext);
        }
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
        int windowHeight = context.getScaledWindowHeight();

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
        if(hud.status != FlounderFestHud.Status.WAVE_CLEAR) {
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
        }

        //"<seconds remaining>" text at the middle left of the screen(beneath wave)
        Text time = Text.literal(String.valueOf(hud.secondsLeft));
        int timeTextWidth = client.textRenderer.getWidth(time);
        int timeX = windowWidth / 2 - titleTextWidth / 2 - timeTextWidth / 2;
        int timeY = 10 + titleY * 4;
        context.drawTextWithShadow(client.textRenderer, time, timeX, timeY, timeColor.getRGB());

        //"<quota progress>/<max quota>" text at the middle right of the screen(beneath quota title)
        Text quota = Text.literal(hud.quotaProgress + "/" + hud.maxQuota);
        int quotaTextWidth = client.textRenderer.getWidth(quota);
        int quotaChar = quotaTextWidth / 6;
        int quotaX = windowWidth / 2 + titleTextWidth / 4 + quotaTextWidth / 8;
        if(quotaChar == 3) { //done because double-digit max quotas(4 characters) is more likely
            quotaX = windowWidth / 2 + titleTextWidth / 4 + quotaTextWidth / 3;
        } else if (quotaChar == 5) {
            quotaX = windowWidth / 2 + titleTextWidth / 4;
        }
        context.drawTextWithShadow(client.textRenderer, quota, quotaX, timeY, quotaColor.getRGB());

        if(shouldPlayQuotaUpdateAnimation) {
            ticksSinceQuotaUpdateAnim++;
            quotaUpdateAnimOpacity = MathHelper.clamp(quotaUpdateAnimOpacity += 0.2f, 0f, 1f);
            if(ticksSinceQuotaUpdateAnim >= 60) {
                quotaUpdateAnimOpacity = MathHelper.clamp(quotaUpdateAnimOpacity -= 0.3f, 0f, 1f);
                if(quotaUpdateAnimOpacity <= 0f) {
                    shouldPlayQuotaUpdateAnimation = false;
                }
            } if (ticksSinceQuotaUpdateAnim >= 100) { //backup in case of bug
                quotaUpdateAnimOpacity = 0f;
                shouldPlayQuotaUpdateAnimation = false;
            }
            Text update = Text.literal("+1");
            int updateWidth = client.textRenderer.getWidth(update);
            int updateHeight = client.textRenderer.getWrappedLinesHeight(update, 114);
            int updateY = timeY + updateHeight;
            RenderSystem.enableBlend();
            context.setShaderColor(1f, 1f, 1f, quotaUpdateAnimOpacity);
            context.drawTextWithShadow(client.textRenderer, update, quotaX, updateY, 16777215);
            context.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }

        if(shouldPlayBossAnim) {
            ticksSinceBossAnim++;
            Text boss = Text.translatable("flutterandflounder.flounderfest.bossalert");
            int bossWidth = client.textRenderer.getWidth(boss);
            int bossHeight = client.textRenderer.getWrappedLinesHeight(boss, 114);
            if(ticksSinceBossAnim >= 100) {
                bossAnimX = MathHelper.clamp(bossAnimX - 7, -bossWidth, bossWidth - 35);
            } else {
                bossAnimX = MathHelper.clamp(2 * ticksSinceBossAnim * ticksSinceBossAnim, -bossWidth, bossWidth - 35);
            }
            context.drawTextWithShadow(client.textRenderer, boss, bossAnimX, timeY, new Color(232, 65, 61).getRGB());
        }

        renderNumbers(context, hud);
        if(!(hud.status == FlounderFestHud.Status.ONGOING)) {
            renderVictoryDefeatOrWaveClear(context, hud);
        }
    }

    public static void renderNumbers(DrawContext context, FlounderFestHud hud) {
        int windowWidth = context.getScaledWindowWidth();
        int windowHeight = context.getScaledWindowHeight();
        if(hud.gracePeriod > 0) {
            Identifier numberTexture = determineNumberTexture(hud.gracePeriod);
            if(numberTexture != null) {
                int numberWidth = 55 * 4;
                int numberHeight = 35 * 4;
                int numberX = windowWidth / 2 - numberWidth / 2;
                int numberY = windowHeight / 2 - numberHeight / 2;
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                context.setShaderColor(.68f, .9f, 1f, .6f);
                context.drawTexture(numberTexture, numberX, numberY, 0, 0, numberWidth, numberHeight, numberWidth, numberHeight);
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
                context.setShaderColor(1f, 1f, 1f, 1f);
            }
        } else if (hud.secondsLeft <= 10) {
            Identifier numberTexture = determineNumberTexture(hud.secondsLeft);
            if(numberTexture != null) {
                int numberWidth = 55 * 4;
                int numberHeight = 35 * 4;
                int numberX = windowWidth / 2 - numberWidth / 2;
                int numberY = windowHeight / 2 - numberHeight / 2;
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                if (hud.quotaProgress >= hud.maxQuota) {
                    context.setShaderColor(.68f, .9f, 1f, .6f);
                } else {
                    context.setShaderColor(.9f, .25f, .23f, .5f);
                }
                context.drawTexture(numberTexture, numberX, numberY, 0, 0, numberWidth, numberHeight, numberWidth, numberHeight);
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
                context.setShaderColor(1f, 1f, 1f, 1f);
            }
        }
    }

    public static Identifier determineNumberTexture(int time) {
        Identifier numberTexture = null;
        switch (time) {
            case 1 -> numberTexture = transparent1;
            case 2 -> numberTexture = transparent2;
            case 3 -> numberTexture = transparent3;
            case 4 -> numberTexture = transparent4;
            case 5 -> numberTexture = transparent5;
            case 6 -> numberTexture = transparent6;
            case 7 -> numberTexture = transparent7;
            case 8 -> numberTexture = transparent8;
            case 9 -> numberTexture = transparent9;
            case 10 -> numberTexture = transparent10;
        }
        return numberTexture;
    }

    public static void renderVictoryDefeatOrWaveClear(DrawContext context, FlounderFestHud hud) {
        switch(hud.status) {
            case WAVE_CLEAR -> {
                Text waveClear = Text.translatable("flutterandflounder.flounderfest.waveclear");
                renderCenteredText(context, waveClear, -20, 7, 280, new Color(175, 238, 255));
            } case VICTORY -> {
                Text victory = Text.translatable("flutterandflounder.flounderfest.victory");
                renderCenteredText(context, victory, -20, 10, 580, new Color(175, 238, 255));
            } case DEFEAT -> {
                Text defeat = Text.translatable("flutterandflounder.flounderfest.defeat");
                renderCenteredText(context, defeat, -20, 7, 580, new Color(232, 65, 61));
            }
        }
    }

    public static void renderCenteredText(DrawContext context, Text text, int y, int typeDelay, int renderTime, Color textColor) {
        MinecraftClient client = MinecraftClient.getInstance();
        int windowWidth = context.getScaledWindowWidth();
        int windowHeight = context.getScaledWindowHeight();

        int textWidth = client.textRenderer.getWidth(text);
        int textX = -textWidth / 2;

        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.translate(windowWidth / 2f, windowHeight / 2f, 0);
        matrixStack.push();
        matrixStack.scale(4f, 4f, 4f);
        if(textTypeWriter == null || renewTextTypeWriter) {
            textTypeWriter = new TextTypeWriter(text, textX, y, typeDelay, renderTime, textColor);
            textTypeWriter.setFadeIn(30);
            textTypeWriter.setFadeOut(20);
            renewTextTypeWriter = false;
        }
        textTypeWriter.writeText(context, matrixStack);
        matrixStack.pop();
        matrixStack.pop();
    }

    public static void startQuotaUpdateAnimation() {
        shouldPlayQuotaUpdateAnimation = true;
        ticksSinceQuotaUpdateAnim = 0;
        quotaUpdateAnimOpacity = 0f;
    }

    public static void playBossAlertAnim() {
        shouldPlayBossAnim = true;
        Text boss = Text.translatable("flutterandflounder.flounderfest.bossalert");
        int bossWidth = MinecraftClient.getInstance().textRenderer.getWidth(boss);
        bossAnimX = -bossWidth;
        ticksSinceBossAnim = 0;
    }

    public static void occupyHudRegion() {
        MinecraftClient client = MinecraftClient.getInstance();
        int windowWidth = client.getWindow().getScaledWidth();
        Text title = Text.translatable("flutterandflounder.flounderfest.title");
        int titleTextWidth = client.textRenderer.getWidth(title);
        Text quotaTitle = Text.translatable("flutterandflounder.flounderfest.quota");

        int x = windowWidth / 2 - titleTextWidth / 2 - titleTextWidth / 4;
        int y = 3;
        int width = titleTextWidth / 4 + client.textRenderer.getWidth(quotaTitle);
        int height = client.textRenderer.getWrappedLinesHeight(title, 114) * 4;
        LibHudCompat.forceOccupyRegion(Identifier.tryParse(MOD_ID), x, y, width, height);
    }

    public static void freeHudRegion() {
        LibHudCompat.freeRegion(Identifier.tryParse(MOD_ID));
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
