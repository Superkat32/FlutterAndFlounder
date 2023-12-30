package net.superkat.flutterandflounder.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;

import static net.superkat.flutterandflounder.FlutterAndFlounderMain.MOD_ID;

public class FlutterAndFlounderItems {

    public static final Item PRISMARINE_DIAMOND = register(
            new PrismarineDiamondItem(new FabricItemSettings()),
            "prismarine_diamond");

    public static final Item PRISMARINE_PEARL = register(
            new Item(new FabricItemSettings()),
            "prismarine_pearl"
    );

    public static final FoodComponent FLOUNDERFEST_COFFEE_COMPONENT = new FoodComponent.Builder()
            .hunger(6)
            .saturationModifier(0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2400, 2), 1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 2400, 2), 1f)
            .alwaysEdible()
            .build();

    public static final Item FLOUNDERFEST_COFFEE = register(
            new FlounderFestCoffeeItem(new FabricItemSettings().rarity(Rarity.RARE).food(FLOUNDERFEST_COFFEE_COMPONENT)),
            "flounderfest_coffee"
    );

    public static final Item FROGMOBILE_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.FROGMOBILE, 0xffffff, 0xffffff, new FabricItemSettings()),
            "frogmobile_spawn_egg"
    );

    public static final Item FLYING_COD_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.FLYING_COD, 0x746550, 0x807668, new FabricItemSettings()),
            "flying_cod_spawn_egg"
    );

    public static final Item FLYING_SALMON_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.FLYING_SALMON, 0x822c2b, 0x56201e, new FabricItemSettings()),
            "flying_salmon_spawn_egg"
    );

    public static final Item CHILL_COD_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.CHILL_COD, 0x746550, 0x2a51e0, new FabricItemSettings()),
            "chillcod_spawn_egg"
    );

    public static final Item CLOWN_COD_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.CLOWN_COD, 0x746550, 0xffffff, new FabricItemSettings()),
            "clowncod_spawn_egg"
    );

    public static final Item COD_AUTOMOBILE_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.COD_AUTOMOBILE, 0x746550, 0xa9aeaf, new FabricItemSettings()),
            "codautomobile_spawn_egg"
    );

    public static final Item COFFEE_COD_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.COFFEE_COD, 0x746550, 0x974d2e, new FabricItemSettings()),
            "coffeecod_spawn_egg"
    );

    public static final Item GOON_COD_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.GOON, 0x746550, 0xb6966b, new FabricItemSettings()),
            "gooncod_spawn_egg"
    );

    public static final Item HAMMER_COD_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.HAMMER_COD, 0x746550, 0xf12424, new FabricItemSettings()),
            "hammercod_spawn_egg"
    );

    public static final Item SALMON_SHIP_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.SALMON_SHIP, 0x822c2b, 0xd3bb50, new FabricItemSettings()),
            "salmonship_spawn_egg"
    );

    public static final Item SALMON_SNIPER_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.SALMON_SNIPER, 0x822c2b, 0x56201e, new FabricItemSettings()),
            "salmonsniper_spawn_egg"
    );

    public static final Item WHACKER_SALMON_SPAWN_EGG = register(
            new SpawnEggItem(FlutterAndFlounderEntities.WHACKER_SALMON, 0x822c2b, 0xf32f2f, new FabricItemSettings()),
            "whackersalmon_spawn_egg"
    );

    public static <T extends Item> T register(T item, String ID) {
        Identifier itemId = new Identifier(MOD_ID, ID);

        T registeredItem = Registry.register(Registries.ITEM, itemId, item);

        return registeredItem;
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.INGREDIENTS)
                .register((itemGroup) -> {
                    itemGroup.add(PRISMARINE_DIAMOND);
                    itemGroup.add(PRISMARINE_PEARL);
                });

        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.SPAWN_EGGS)
                .register((itemGroup) -> {
                    itemGroup.add(FROGMOBILE_SPAWN_EGG);
                    itemGroup.add(FLYING_COD_SPAWN_EGG);
                    itemGroup.add(FLYING_SALMON_SPAWN_EGG);
                    itemGroup.add(CHILL_COD_SPAWN_EGG);
                    itemGroup.add(CLOWN_COD_SPAWN_EGG);
                    itemGroup.add(COD_AUTOMOBILE_SPAWN_EGG);
                    itemGroup.add(COFFEE_COD_SPAWN_EGG);
                    itemGroup.add(GOON_COD_SPAWN_EGG);
                    itemGroup.add(HAMMER_COD_SPAWN_EGG);
                    itemGroup.add(SALMON_SHIP_SPAWN_EGG);
                    itemGroup.add(SALMON_SNIPER_SPAWN_EGG);
                    itemGroup.add(WHACKER_SALMON_SPAWN_EGG);
                });

        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(FLOUNDERFEST_COFFEE));
    }

}
