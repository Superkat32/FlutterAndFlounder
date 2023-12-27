package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.cod.GoonCodEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GoonCodRenderer extends GeoEntityRenderer<GoonCodEntity> {
    public GoonCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GoonCodModel());
    }
}
