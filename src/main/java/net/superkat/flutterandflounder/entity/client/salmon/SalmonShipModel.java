package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonShipEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SalmonShipModel extends DefaultedEntityGeoModel<SalmonShipEntity> {
    public SalmonShipModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "salmonship"), false);
    }
}
