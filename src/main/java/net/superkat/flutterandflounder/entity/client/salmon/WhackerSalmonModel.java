package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.salmon.WhackerSalmonEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WhackerSalmonModel extends DefaultedEntityGeoModel<WhackerSalmonEntity> {
    public WhackerSalmonModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "whacker"), true);
    }
}
