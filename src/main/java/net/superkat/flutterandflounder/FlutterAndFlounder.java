package net.superkat.flutterandflounder;

import net.fabricmc.api.ModInitializer;
import net.superkat.flounderlib.api.minigame.v1.FlounderApi;
import net.superkat.flounderlib.api.minigame.v1.registry.FlounderGameType;
import net.superkat.flutterandflounder.command.autofill.FlounderFestGameAutofill;
import net.superkat.flutterandflounder.game.FlounderFestGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlutterAndFlounder implements ModInitializer {
	public static final String MOD_ID = "flutterandflounder";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final FlounderGameType<FlounderFestGame> FLOUNDER_FEST_GAME_TYPE = FlounderApi.register(
            FlounderGameType.create(FlounderFestGame.ID, FlounderFestGame.CODEC)
                    .synced(FlounderFestGame.STATE_SYNCER)
                    .overlap(false)
                    .distance(64)
                    .padding(32) // TODO - in Flounderlib, account for semicircle during overlap check
    );

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

        FlounderFestGameAutofill.init();
	}
}