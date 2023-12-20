package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonShipEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SalmonShipRenderer extends GeoEntityRenderer<SalmonShipEntity> {
    public SalmonShipRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SalmonShipModel());
    }
}
