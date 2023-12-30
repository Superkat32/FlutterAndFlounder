package net.superkat.flutterandflounder.entity.client.frogmobile;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.frogmobile.FrogmobileEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FrogmobileModel extends DefaultedEntityGeoModel<FrogmobileEntity> {
    public FrogmobileModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "frogmobile"), false);
    }
}
