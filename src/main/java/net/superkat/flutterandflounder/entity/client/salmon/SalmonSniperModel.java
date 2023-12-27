package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonSniperEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SalmonSniperModel extends DefaultedEntityGeoModel<SalmonSniperEntity> {
    public SalmonSniperModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "salmonsniper"), true);
    }
}
