package com.manjusaka.item;

import com.manjusaka.TutorialMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item WORKING_TABLE_PASSPORT = registerItem("working_table_passport", new Item(new Item.Settings().maxCount(12)));

    public static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM,RegistryKey.of(Registries.ITEM.getKey(), new Identifier(TutorialMod.MOD_ID,id)),  item);
    }

    private static void addItemToIG(FabricItemGroupEntries fabricItemGroupEntries){
        fabricItemGroupEntries.add(WORKING_TABLE_PASSPORT);
    }

    // 初始化方法
    public static void registerModItems(){
        // 通过Fabric的ItemGroupEvents添加物品
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemToIG);
    }

    public static void registerItems(){

    }
}
