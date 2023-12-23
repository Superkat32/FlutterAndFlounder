package net.superkat.flutterandflounder.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

public class FlutterAndFlounderRendering {
    private final MinecraftClient client;
    public static boolean shouldChangeSky = false;
    public static double skyChangeMultiplier = 0;
    @Nullable
    public static FlounderFestHud flounderFestHud = null;

    public FlutterAndFlounderRendering(MinecraftClient client) {
        this.client = client;
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
        System.out.println("time: " + hud.secondsLeft);
        System.out.println("grace: " + hud.gracePeriod);
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
