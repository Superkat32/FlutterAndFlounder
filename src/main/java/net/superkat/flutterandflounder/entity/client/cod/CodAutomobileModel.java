package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.CodAutomobileEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CodAutomobileModel extends DefaultedEntityGeoModel<CodAutomobileEntity> {
    public CodAutomobileModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "codautomobile"), false);
    }
}
