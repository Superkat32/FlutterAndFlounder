package net.superkat.flutterandflounder.client.render.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.superkat.flounderlib.api.minigame.v1.FlounderClientApi;
import net.superkat.flutterandflounder.FlutterAndFlounder;
import net.superkat.flutterandflounder.game.FlounderFestGame;

public class FlounderFestGuiRenderer {

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if(!FlounderClientApi.anyMinigames(FlutterAndFlounder.FLOUNDER_FEST_GAME_TYPE)) return;

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        FlounderFestGame.SyncState syncState = FlounderClientApi.getFirstSyncState(FlounderFestGame.STATE_SYNCER);
        int windowWidth = graphics.guiWidth();
        int windowHeight = graphics.guiHeight();

        Component title = Component.translatable("flutterandflounder.flounderfest.title");
        int ticks = syncState.ticks;
        int waveCount = syncState.waveCount;

        graphics.drawCenteredString(font, title, windowWidth / 2, 3, CommonColors.WHITE);
        graphics.drawCenteredString(font, String.valueOf(ticks), windowWidth / 2, 13, CommonColors.WHITE);
    }

}
