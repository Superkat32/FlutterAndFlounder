package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.cod.ChillCodEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChillCodRenderer extends GeoEntityRenderer<ChillCodEntity> {
    public ChillCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChillCodModel());
    }
}
