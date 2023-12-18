package net.superkat.flutterandflounder.entity.client.salmon;

import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.salmon.FlyingSalmonEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FlyingSalmonModel extends DefaultedEntityGeoModel<FlyingSalmonEntity> {
    public FlyingSalmonModel() {
        super(new Identifier(FlutterAndFlounderMain.MOD_ID, "flyingsalmon"), false);
    }
}
