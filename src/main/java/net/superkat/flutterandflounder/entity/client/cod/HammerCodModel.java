package net.superkat.flutterandflounder.entity.client.cod;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.HammerCodEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HammerCodModel extends DefaultedEntityGeoModel<HammerCodEntity> {
    public HammerCodModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "hammercod"), false);
    }
}
