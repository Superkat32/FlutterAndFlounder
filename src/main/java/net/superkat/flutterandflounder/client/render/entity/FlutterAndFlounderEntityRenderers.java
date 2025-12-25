package net.superkat.flutterandflounder.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.superkat.flutterandflounder.client.render.entity.boss.HammerCodRenderer;
import net.superkat.flutterandflounder.client.render.entity.collectables.PearlescentEggRenderer;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntityTypes;

public class FlutterAndFlounderEntityRenderers {

    public static void init() {
        EntityRenderers.register(FlutterAndFlounderEntityTypes.PEARLESCENT_EGG, PearlescentEggRenderer::new);

        EntityRenderers.register(FlutterAndFlounderEntityTypes.HAMMER_COD, HammerCodRenderer::new);
    }

}
