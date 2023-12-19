package net.superkat.flutterandflounder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.custom.cod.FlyingCodEntity;
import net.superkat.flutterandflounder.entity.custom.salmon.FlyingSalmonEntity;
import net.superkat.flutterandflounder.flounderfest.command.FlounderFestCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class FlutterAndFlounderMain implements ModInitializer {
	public static final String MOD_ID = "flutterandflounder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		GeckoLib.initialize();

		FabricDefaultAttributeRegistry.register(FlutterAndFlounderEntities.FLYING_COD, FlyingCodEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(FlutterAndFlounderEntities.FLYING_SALMON, FlyingSalmonEntity.createAttributes());

		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> FlounderFestCommand.register(dispatcher)));

		LOGGER.info("Hello Fabric world!");
	}
}