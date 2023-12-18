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

public class ModEntities {
    public static final EntityType<FlyingCodEntity> FLYING_COD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(FlutterAndFlounderMain.MOD_ID, "flyingcod"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FlyingCodEntity::new)
                    .dimensions(EntityDimensions.fixed(1.2f, 0.5f)).build()
    );

}
