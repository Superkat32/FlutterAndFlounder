package net.superkat.flutterandflounder.client.render.entity.collectables;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounder;
import net.superkat.flutterandflounder.entity.collectables.PearlescentEgg;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class PearlescentEggRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<PearlescentEgg, R> {

    public PearlescentEggRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());

        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.withScale(1.25f);
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.itemEntityTranslucentCull(texture);
    }

    public static class Model extends DefaultedEntityGeoModel<PearlescentEgg> {
        public Model() {
            super(Identifier.fromNamespaceAndPath(FlutterAndFlounder.MOD_ID, "pearlescent_egg"));
        }
    }

}
