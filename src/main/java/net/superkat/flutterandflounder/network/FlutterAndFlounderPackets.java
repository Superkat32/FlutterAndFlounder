package net.superkat.flutterandflounder.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.rendering.FlounderFestHud;
import net.superkat.flutterandflounder.rendering.FlutterAndFlounderRendering;

import static net.superkat.flutterandflounder.FlutterAndFlounderMain.LOGGER;
import static net.superkat.flutterandflounder.FlutterAndFlounderMain.MOD_ID;

public class FlutterAndFlounderPackets {
    public static final Identifier FLOUNDERFEST_CREATE_HUD_ID = new Identifier(MOD_ID, "flounderfest_create_hud");
    public static final Identifier FLOUNDERFEST_GRACE_PERIOD_ID = new Identifier(MOD_ID, "flounderfest_grace_period");
    public static final Identifier FLOUNDERFEST_WAVE_UPDATE_ID = new Identifier(MOD_ID, "flounderfest_wave_update");
    public static final Identifier FLOUNDERFEST_TIMER_UPDATE_ID = new Identifier(MOD_ID, "flounderfest_timer_update");
    public static final Identifier FLOUNDERFEST_QUOTA_PROGRESS_UPDATE_ID = new Identifier(MOD_ID, "flounderfest_quota_progress_update");
    public static final Identifier FLOUNDERFEST_REMOVE_HUD_ID = new Identifier(MOD_ID, "flounderfest_remove_hud");

    public static void registerPackets() {


        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_CREATE_HUD_ID, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int waveNum = buf.readInt();
                int maxWaves = buf.readInt();
                int secondsLeft = buf.readInt();
                int quotaProgress = buf.readInt();
                int maxQuota = buf.readInt();
                FlutterAndFlounderRendering.flounderFestHud = new FlounderFestHud(waveNum, maxWaves, secondsLeft, quotaProgress, maxQuota);
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_GRACE_PERIOD_ID, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int gracePeriod = buf.readInt();
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) {
                    hud.updateGracePeriod(gracePeriod);
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_TIMER_UPDATE_ID, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int secondsRemaining = buf.readInt();
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) {
                    hud.updateTimer(secondsRemaining);
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_REMOVE_HUD_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                FlutterAndFlounderRendering.flounderFestHud = null;
                LOGGER.info("Removing FlounderFest hud!");
            });
        })));
    }
}
