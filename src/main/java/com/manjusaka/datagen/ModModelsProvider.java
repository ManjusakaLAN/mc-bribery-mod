package com.manjusaka.datagen;

import com.manjusaka.block.ModBlocks;
import com.manjusaka.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

import java.util.Optional;

public class ModModelsProvider extends FabricModelProvider {


    public ModModelsProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ICE_ETHER_BLOCK);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.WORKING_TABLE_PASSPORT, Models.GENERATED);


        // 添加这一行，为 ICE_ETHER_BLOCK 的 Item 生成模型
        itemModelGenerator.register(
                ModBlocks.ICE_ETHER_BLOCK.asItem(),
                new Model(Optional.of(ModelIds.getBlockModelId(ModBlocks.ICE_ETHER_BLOCK)), Optional.empty())
        );
    }
}
