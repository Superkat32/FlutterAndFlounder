package net.superkat.flutterandflounder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.client.cod.CodAutomobileRenderer;
import net.superkat.flutterandflounder.entity.client.cod.FlyingCodRenderer;
import net.superkat.flutterandflounder.entity.client.salmon.FlyingSalmonRenderer;
import net.superkat.flutterandflounder.entity.client.salmon.SalmonShipRenderer;
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

        FlutterAndFlounderPackets.registerPackets();

        FlutterAndFlounderRendering.registerHudEvents();

//        ClientPlayNetworking.registerGlobalReceiver(FlutterAndFlounderPackets.FLOUNDERFEST_TIMER_UPDATE_ID, ((client, handler, buf, responseSender) -> {
//            client.execute(() -> {
//                int timeR = buf.readInt();
//                System.out.println("packet received!");
//                System.out.println("Time remaining: " + timeR);
//            });
//        }));
    }
}
