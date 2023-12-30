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
                .register((itemGroup) -> itemGroup.add(FROGMOBILE_SPAWN_EGG));

        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(FLOUNDERFEST_COFFEE));
    }

}
