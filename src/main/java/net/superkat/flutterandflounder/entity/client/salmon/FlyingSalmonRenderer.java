package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.superkat.flutterandflounder.entity.custom.salmon.FlyingSalmonEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FlyingSalmonRenderer extends GeoEntityRenderer<FlyingSalmonEntity> {
    public FlyingSalmonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FlyingSalmonModel());
    }
}
