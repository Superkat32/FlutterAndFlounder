package net.superkat.flutterandflounder.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

public class FlutterAndFlounderRendering {
    private final MinecraftClient client;
    public static boolean shouldChangeSky = false;

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
            FlutterAndFlounderRendering.shouldChangeSky = true;
            int wave = flounderFestHud.waveNum;
            int secondsRemaining = flounderFestHud.secondsLeft;
            int quotaProgress = flounderFestHud.quotaProgress;
        }
    }

    public void setFlounderFestHud(@Nullable FlounderFestHud flounderFestHud) {
        FlutterAndFlounderRendering.flounderFestHud = flounderFestHud;
        shouldChangeSky = flounderFestHud != null;
    }

    @Nullable
    public FlounderFestHud getCurrentHud() {
        return flounderFestHud;
    }

}
