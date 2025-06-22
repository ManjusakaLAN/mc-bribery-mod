package com.manjusaka;

import com.manjusaka.datagen.ModBlockTagsProvider;
import com.manjusaka.datagen.ModEnCnLangProvider;
import com.manjusaka.datagen.ModEnUsLangProvider;
import com.manjusaka.datagen.ModModelsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TutorialModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();


        pack.addProvider(ModEnUsLangProvider::new);
        pack.addProvider(ModEnCnLangProvider::new);

        pack.addProvider(ModBlockTagsProvider::new);
        pack.addProvider(ModModelsProvider::new);

    }
}
