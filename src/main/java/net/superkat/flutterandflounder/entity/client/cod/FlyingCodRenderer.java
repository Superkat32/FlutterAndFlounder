package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.cod.FlyingCodEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FlyingCodRenderer extends GeoEntityRenderer<FlyingCodEntity> {
    public FlyingCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FlyingCodModel());
    }
}
