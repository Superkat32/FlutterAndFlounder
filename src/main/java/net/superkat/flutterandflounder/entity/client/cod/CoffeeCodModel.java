package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.CoffeeCodEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CoffeeCodModel extends DefaultedEntityGeoModel<CoffeeCodEntity> {
    public CoffeeCodModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "coffeecod"), true);
    }
}
