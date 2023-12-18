package net.superkat.flutterandflounder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.superkat.flutterandflounder.entity.ModEntities;
import net.superkat.flutterandflounder.entity.custom.cod.FlyingCodEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class FlutterAndFlounderMain implements ModInitializer {
	public static final String MOD_ID = "flutterandflounder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



	@Override
	public void onInitialize() {

		GeckoLib.initialize();

		FabricDefaultAttributeRegistry.register(ModEntities.FLYING_COD, FlyingCodEntity.createAttributes());

		LOGGER.info("Hello Fabric world!");
	}
}