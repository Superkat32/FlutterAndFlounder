package net.superkat.flutterandflounder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.client.cod.FlyingCodRenderer;
import net.superkat.flutterandflounder.entity.client.salmon.FlyingSalmonRenderer;

public class FlutterAndFlounderClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(FlutterAndFlounderEntities.FLYING_COD, FlyingCodRenderer::new);
        EntityRendererRegistry.register(FlutterAndFlounderEntities.FLYING_SALMON, FlyingSalmonRenderer::new);
    }
}
