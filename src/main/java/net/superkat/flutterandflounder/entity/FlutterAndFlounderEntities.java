package net.superkat.flutterandflounder.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.superkat.flutterandflounder.entity.custom.cod.*;
import net.superkat.flutterandflounder.entity.custom.salmon.FlyingSalmonEntity;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonShipEntity;
import net.superkat.flutterandflounder.entity.custom.salmon.SalmonSniperEntity;
import net.superkat.flutterandflounder.entity.custom.salmon.WhackerSalmonEntity;

import static net.superkat.flutterandflounder.FlutterAndFlounderMain.MOD_ID;

public class FlutterAndFlounderEntities {
    public static final EntityType<FlyingCodEntity> FLYING_COD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "flyingcod"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FlyingCodEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.8f)).build()
    );

    public static final EntityType<FlyingSalmonEntity> FLYING_SALMON = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "flyingsalmon"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FlyingSalmonEntity::new)
                    .dimensions(EntityDimensions.fixed(1.3f, 0.9f)).build()
    );

    //bosses

    public static final EntityType<CodAutomobileEntity> COD_AUTOMOBILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "codautomobile"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, CodAutomobileEntity::new)
                    .dimensions(EntityDimensions.fixed(1.2f, 0.9f)).build()
    );

    public static final EntityType<SalmonShipEntity> SALMON_SHIP = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "salmonship"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SalmonShipEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1f)).build()
    );

    public static final EntityType<HammerCodEntity> HAMMER_COD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "hammercod"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, HammerCodEntity::new)
                    .dimensions(EntityDimensions.fixed(2f, 3.2f)).build()
    );

    public static final EntityType<WhackerSalmonEntity> WHACKER_SALMON = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "whacker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, WhackerSalmonEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 0.9f)).build()
    );

    public static final EntityType<ChillCodEntity> CHILL_COD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "chilldcod"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ChillCodEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.8f)).build()
    );

    public static final EntityType<SalmonSniperEntity> SALMON_SNIPER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "salmonsniper"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SalmonSniperEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 1.3f)).build()
    );

    public static final EntityType<ClownCodEntity> CLOWN_COD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "clowncod"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ClownCodEntity::new)
                    .dimensions(EntityDimensions.fixed(0.9f, 0.8f)).build()
    );

    public static final EntityType<GoonCodEntity> GOON = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "goon"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GoonCodEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f)).build()
    );

}
