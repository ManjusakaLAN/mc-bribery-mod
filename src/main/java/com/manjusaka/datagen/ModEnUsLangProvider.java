package com.manjusaka.datagen;

import com.manjusaka.block.ModBlocks;
import com.manjusaka.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModEnUsLangProvider extends FabricLanguageProvider {
    public ModEnUsLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput,"en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {

        translationBuilder.add(ModItems.WORKING_TABLE_PASSPORT,"WORKING TABLE PASSPORT");

        translationBuilder.add(ModBlocks.ICE_ETHER_BLOCK, "Ice Ether Block");

        translationBuilder.add("itemGroup.tutorial_group", "Tutorial Mod");
    }
}
