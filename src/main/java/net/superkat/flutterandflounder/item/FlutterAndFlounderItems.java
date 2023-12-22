package net.superkat.flutterandflounder.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.superkat.flutterandflounder.FlutterAndFlounderMain.MOD_ID;

public class FlutterAndFlounderItems {

    public static final Item PRISMARINE_DIAMOND = register(
            new PrismarineDiamondItem(new FabricItemSettings()),
            "prismarine_diamond");

    public static <T extends Item> T register(T item, String ID) {
        Identifier itemId = new Identifier(MOD_ID, ID);

        T registeredItem = Registry.register(Registries.ITEM, itemId, item);

        return registeredItem;
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(
                ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(PRISMARINE_DIAMOND));
    }

}
