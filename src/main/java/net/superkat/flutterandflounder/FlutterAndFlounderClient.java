package net.superkat.flutterandflounder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;
import net.superkat.flutterandflounder.client.render.entity.boss.HammerCodRenderer;
import net.superkat.flutterandflounder.client.render.gui.FlounderFestGuiRenderer;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntityTypes;

public class FlutterAndFlounderClient implements ClientModInitializer {

    public static final Identifier FLOUNDER_FEST_GUI_LAYER = Identifier.fromNamespaceAndPath(FlutterAndFlounder.MOD_ID, "flounderfest_gui_layer");

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, FLOUNDER_FEST_GUI_LAYER, FlounderFestGuiRenderer::render);

        EntityRenderers.register(FlutterAndFlounderEntityTypes.HAMMER_COD, HammerCodRenderer::new);
    }
}
