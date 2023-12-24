package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.cod.CodAutomobileEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CodAutomobileRenderer extends GeoEntityRenderer<CodAutomobileEntity> {
    public CodAutomobileRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CodAutomobileModel());
    }
}
