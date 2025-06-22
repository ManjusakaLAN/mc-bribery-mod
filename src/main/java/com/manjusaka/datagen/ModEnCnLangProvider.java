package com.manjusaka.datagen;

import com.manjusaka.block.ModBlocks;
import com.manjusaka.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModEnCnLangProvider extends FabricLanguageProvider {
    public ModEnCnLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"zh_cn");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {

        translationBuilder.add(ModItems.WORKING_TABLE_PASSPORT,"合成许可证");

        translationBuilder.add(ModBlocks.ICE_ETHER_BLOCK, "冰块");

        translationBuilder.add("itemGroup.tutorial_group", "许可证");
    }
}
