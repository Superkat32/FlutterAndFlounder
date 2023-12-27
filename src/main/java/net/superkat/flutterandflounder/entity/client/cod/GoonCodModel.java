package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.GoonCodEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GoonCodModel extends DefaultedEntityGeoModel<GoonCodEntity> {
    public GoonCodModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "goon"), true);
    }
}
