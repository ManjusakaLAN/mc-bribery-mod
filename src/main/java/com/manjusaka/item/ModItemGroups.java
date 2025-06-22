package com.manjusaka.item;

import com.manjusaka.TutorialMod;
import com.manjusaka.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    // 原版物品栏注册方法
//    public static final RegistryKey<ItemGroup> TUTORIAL_GROUP = register("tutorial_group");
//    private static RegistryKey<ItemGroup> register(String id) {
//        return RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(TutorialMod.MOD_ID, id));
//    }
//    public static void registerModItemGroups() {
//        Registry.register(Registries.ITEM_GROUP, TUTORIAL_GROUP,
//                ItemGroup.create(ItemGroup.Row.TOP, 7)
//                                .displayName(Text.translatable("itemGroup.tutorial-mod.tutorial_group"))
//                                        .icon(() -> new ItemStack(ModItems.ICE_ETHER))
//                        .entries((displayContext, entries) -> {
//                            entries.add(ModItems.ICE_ETHER);
//                        }).build());
//        TutorialMod.LOGGER.info("Registering Item Groups");
//    }
    // 利用返回值为ItemGroup，直接使用static final变量进行注册
    public static final ItemGroup TUTORIAL_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(TutorialMod.MOD_ID, "tutorial_group"),
            ItemGroup.create(null, -1).displayName(Text.translatable("itemGroup.tutorial_group"))
                    .icon(() -> new ItemStack(ModItems.WORKING_TABLE_PASSPORT))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.WORKING_TABLE_PASSPORT);
                        entries.add(ModBlocks.ICE_ETHER_BLOCK);
                        entries.add(Blocks.BRICKS);
                        entries.add(Items.DIAMOND);
                    }).build());

    // 初始化方法
    public static void registerModItemGroups() {
        TutorialMod.LOGGER.info("Registering Item Groups");
    }
}
