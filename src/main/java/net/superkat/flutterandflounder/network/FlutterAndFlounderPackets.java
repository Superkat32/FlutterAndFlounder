package net.superkat.flutterandflounder.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.superkat.flutterandflounder.rendering.FlounderFestCenterRenderer;
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
    public static final Identifier FLOUNDERFEST_BOSS_ALERT_ID = new Identifier(MOD_ID, "flounderfest_boss_alert");
    public static final Identifier FLOUNDERFEST_VICTORY_ID = new Identifier(MOD_ID, "flounderfest_victory");
    public static final Identifier FLOUNDERFEST_DEFEAT_ID = new Identifier(MOD_ID, "flounderfest_defeat");
    public static final Identifier FLOUNDERFEST_WAVE_CLEAR_ID = new Identifier(MOD_ID, "flounderfest_wave_clear");

    public static void registerPackets() {


        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_CREATE_HUD_ID, ((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int waveNum = buf.readInt();
                int maxWaves = buf.readInt();
                int secondsLeft = buf.readInt();
                int quotaProgress = buf.readInt();
                int maxQuota = buf.readInt();
                BlockPos startingPos = buf.readBlockPos();
                FlutterAndFlounderRendering.occupyHudRegion();
                FlutterAndFlounderRendering.flounderFestHud = new FlounderFestHud(waveNum, maxWaves, secondsLeft, quotaProgress, maxQuota);
                FlutterAndFlounderRendering.flounderFestCenterRenderer = new FlounderFestCenterRenderer(startingPos);
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

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_QUOTA_PROGRESS_UPDATE_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int quotaProgress = buf.readInt();
                int maxQuota = buf.readInt();
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) {
                    hud.updateQuota(quotaProgress, maxQuota);
                }
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_WAVE_UPDATE_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                int wave = buf.readInt();
                int maxWaves = buf.readInt();
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) {
                    hud.updateWave(wave, maxWaves);
                    hud.status = FlounderFestHud.Status.ONGOING;
                }
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_REMOVE_HUD_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                FlutterAndFlounderRendering.freeHudRegion();
                if(FlutterAndFlounderRendering.flounderFestCenterRenderer != null) {
                    LOGGER.info("Removing FlounderFest hud!");
                }
                FlutterAndFlounderRendering.flounderFestHud = null;
                FlutterAndFlounderRendering.flounderFestCenterRenderer = null;
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_WAVE_CLEAR_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) {
                    hud.updateStatus(FlounderFestHud.Status.WAVE_CLEAR);
                }
                client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 1f);
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_BOSS_ALERT_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) { //confirms a FlounderFestHud is active
                    FlutterAndFlounderRendering.playBossAlertAnim();
                }
                client.player.playSound(SoundEvents.BLOCK_BELL_USE, SoundCategory.MASTER, 1f, 1f);
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_VICTORY_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;

                if(hud != null) {
                    hud.updateStatus(FlounderFestHud.Status.VICTORY);
                }
                client.player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1f, 1f);
            });
        })));

        ClientPlayNetworking.registerGlobalReceiver(FLOUNDERFEST_DEFEAT_ID, (((client, handler, buf, responseSender) -> {
            client.execute(() -> {
                FlounderFestHud hud = FlutterAndFlounderRendering.flounderFestHud;
                if(hud != null) {
                    hud.updateStatus(FlounderFestHud.Status.DEFEAT);
                }
                client.player.playSound(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 1f, 0.5f);
            });
        })));
    }
}
