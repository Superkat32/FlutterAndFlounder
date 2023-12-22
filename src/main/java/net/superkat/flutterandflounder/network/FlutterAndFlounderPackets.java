package net.superkat.flutterandflounder.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.rendering.FlounderFestHud;
import net.superkat.flutterandflounder.rendering.FlutterAndFlounderRendering;

import static net.superkat.flutterandflounder.FlutterAndFlounderMain.MOD_ID;

public class FlutterAndFlounderPackets {
    public static final Identifier FLOUNDERFEST_CREATE_HUD_ID = new Identifier(MOD_ID, "flounderfest_create_hud");
    public static final Identifier FLOUNDERFEST_WAVE_UPDATE_ID = new Identifier(MOD_ID, "flounderfest_wave_update");
    public static final Identifier FLOUNDERFEST_TIMER_UPDATE_ID = new Identifier(MOD_ID, "flounderfest_timer_update");
    public static final Identifier FLOUNDERFEST_QUOTA_PROGRESS_UPDATE_ID = new Identifier(MOD_ID, "flounderfest_quota_progress_update");
    public static final Identifier FLOUNDERFEST_REMOVE_HUD_ID = new Identifier(MOD_ID, "flounderfest_remove_hud");

    public static void registerPackets() {

//        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_CREATE_HUD_ID, (((client, handler, buf, responseSender) -> {
//            client.execute(() -> {
//
//            });
//        })))

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_TIMER_UPDATE_ID, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int wave = buf.readInt();
                int secondsRemaining = buf.readInt();
                int quotaProgress = buf.readInt();
                if(FlutterAndFlounderRendering.flounderFestHud != null) {
                    FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                    hud.updateInfo(wave, secondsRemaining, quotaProgress);
                } else {
                    FlutterAndFlounderRendering.flounderFestHud = new FlounderFestHud(wave, secondsRemaining, quotaProgress);
                }
            });
        }));
    }
}
