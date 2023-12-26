package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.ChillCodEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ChillCodModel extends DefaultedEntityGeoModel<ChillCodEntity> {
    public ChillCodModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "chillcod"), true);
    }
}
