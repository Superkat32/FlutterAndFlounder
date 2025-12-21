package net.superkat.flutterandflounder.client.render.entity.boss;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounder;
import net.superkat.flutterandflounder.entity.boss.HammerCod;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class HammerCodRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<HammerCod, R> {

    public HammerCodRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
    }

    public static class Model extends DefaultedEntityGeoModel<HammerCod> {

        public Model() {
            super(Identifier.fromNamespaceAndPath(FlutterAndFlounder.MOD_ID, "hammercod"));
        }
    }

}
