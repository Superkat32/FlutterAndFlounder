package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.cod.HammerCodEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HammerCodRenderer extends GeoEntityRenderer<HammerCodEntity> {
    public HammerCodRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HammerCodModel());
    }
}
