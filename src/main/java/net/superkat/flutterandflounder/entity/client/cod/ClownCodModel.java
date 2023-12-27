package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.ClownCodEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ClownCodModel extends DefaultedEntityGeoModel<ClownCodEntity> {
    public ClownCodModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "clowncod"), true);
    }
}
