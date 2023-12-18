package net.superkat.flutterandflounder.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.custom.cod.FlyingCodEntity;
import net.superkat.flutterandflounder.entity.custom.salmon.FlyingSalmonEntity;

public class FlutterAndFlounderEntities {
    public static final EntityType<FlyingCodEntity> FLYING_COD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(FlutterAndFlounderMain.MOD_ID, "flyingcod"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FlyingCodEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.5f)).build()
    );

    public static final EntityType<FlyingSalmonEntity> FLYING_SALMON = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(FlutterAndFlounderMain.MOD_ID, "flyingsalmon"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FlyingSalmonEntity::new)
                    .dimensions(EntityDimensions.fixed(1.3f, 0.6f)).build()
    );

}
