package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.FlyingCodEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FlyingCodModel extends DefaultedEntityGeoModel<FlyingCodEntity> {
    public FlyingCodModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "flyingcod"), false);
    }
}
