package com.manjusaka;

import com.manjusaka.block.ModBlocks;
import com.manjusaka.constants.RoleEnum;
import com.manjusaka.datapersist.BriberyTaskInfoData;
import com.manjusaka.datapersist.PlayerInfoData;

import com.manjusaka.datapersist.WorldProperties;
import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.event.EventRegister;
import com.manjusaka.item.ModItemGroups;
import com.manjusaka.item.ModItems;
import com.manjusaka.network.ServerNetworkRegister;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;


public class TutorialMod implements ModInitializer {
    public static final String MOD_ID = "tutorial-mod";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        /*
         * 注册所有事件
         * @ 玩家加入事件
         * @ 按键绑定事件
         */
        EventRegister.registerAllEvents();
        // 注册网络隧道
        ServerNetworkRegister.serverRegister();
        // 初始化
        ModItems.registerItems();
        ModItemGroups.registerModItemGroups();
        ModBlocks.registerModBlocks();

    }

}