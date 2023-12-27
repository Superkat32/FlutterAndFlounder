package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonSniperEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SalmonSniperRenderer extends GeoEntityRenderer<SalmonSniperEntity> {
    public SalmonSniperRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SalmonSniperModel());
    }
}
