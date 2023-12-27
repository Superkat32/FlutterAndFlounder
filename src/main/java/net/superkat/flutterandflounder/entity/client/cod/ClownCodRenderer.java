package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.cod.ClownCodEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ClownCodRenderer extends GeoEntityRenderer<ClownCodEntity> {
    public ClownCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ClownCodModel());
    }
}
