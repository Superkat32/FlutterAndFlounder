package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.salmon.WhackerSalmonEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WhackerSalmonRenderer extends GeoEntityRenderer<WhackerSalmonEntity> {
    public WhackerSalmonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WhackerSalmonModel());
    }
}
