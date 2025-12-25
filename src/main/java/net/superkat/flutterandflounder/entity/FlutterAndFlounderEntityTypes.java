package net.superkat.flutterandflounder.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.superkat.flutterandflounder.FlutterAndFlounder;
import net.superkat.flutterandflounder.entity.boss.HammerCod;
import net.superkat.flutterandflounder.entity.collectables.PearlescentEgg;

public class FlutterAndFlounderEntityTypes {

    // region Collectables
    public static final EntityType<PearlescentEgg> PEARLESCENT_EGG = register(
            "pearlescent_egg",
            EntityType.Builder
                    .of(PearlescentEgg::new, MobCategory.MISC)
                    .sized(0.65f, 0.65f)
                    .clientTrackingRange(6)
                    .updateInterval(20)
    );

    // endregion

    // region Bosses
    public static final EntityType<HammerCod> HAMMER_COD = register(
            "hammercod",
            EntityType.Builder
                    .of(HammerCod::new, MobCategory.MONSTER)
                    .sized(1.8f, 3f)
                    .clientTrackingRange(4)
    );

    // endregion

    public static void init() {
        FabricDefaultAttributeRegistry.register(HAMMER_COD, HammerCod.createMobAttributes());
        FabricDefaultAttributeRegistry.register(PEARLESCENT_EGG, PearlescentEgg.createMobAttributes());
    }

    // Stolen from Abysm - https://github.com/SpiritGameStudios/Abysm/blob/main/src/main/java/dev/spiritstudios/abysm/world/entity/AbysmEntityTypes.java#L156
    private static <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }

    private static ResourceKey<EntityType<?>> keyOf(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(FlutterAndFlounder.MOD_ID, id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

}
