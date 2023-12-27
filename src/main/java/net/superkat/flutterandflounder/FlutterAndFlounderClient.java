package net.superkat.flutterandflounder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.client.cod.*;
import net.superkat.flutterandflounder.entity.client.salmon.FlyingSalmonRenderer;
import net.superkat.flutterandflounder.entity.client.salmon.SalmonShipRenderer;
import net.superkat.flutterandflounder.entity.client.salmon.SalmonSniperRenderer;
import net.superkat.flutterandflounder.entity.client.salmon.WhackerSalmonRenderer;
import net.superkat.flutterandflounder.network.FlutterAndFlounderPackets;
import net.superkat.flutterandflounder.rendering.FlutterAndFlounderRendering;

public class FlutterAndFlounderClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(FlutterAndFlounderEntities.FLYING_COD, FlyingCodRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.FLYING_SALMON, FlyingSalmonRenderer::new);

        //bosses
        EntityRendererRegistry.register(FlutterAndFlounderEntities.COD_AUTOMOBILE, CodAutomobileRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.SALMON_SHIP, SalmonShipRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.HAMMER_COD, HammerCodRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.WHACKER_SALMON, WhackerSalmonRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.CHILL_COD, ChillCodRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.SALMON_SNIPER, SalmonSniperRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.CLOWN_COD, ClownCodRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.GOON, GoonCodRenderer::new);

        FlutterAndFlounderPackets.registerPackets();

        FlutterAndFlounderRendering.registerHudEvents();
        FlutterAndFlounderRendering.registerWorldEvents();
    }
}
