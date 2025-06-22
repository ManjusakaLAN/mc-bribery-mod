package com.manjusaka;

import com.manjusaka.block.ModBlocks;
import com.manjusaka.constants.RoleEnum;
import com.manjusaka.datapersist.BriberyTaskInfoData;
import com.manjusaka.datapersist.PlayerInfoData;

import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.event.EventRegister;
import com.manjusaka.item.ModItemGroups;
import com.manjusaka.item.ModItems;
import com.manjusaka.network.ServerNetworkRegister;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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




        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("refreshOfficial").executes(context -> {
//                        PlayInfoData state = PlayInfoData.get(context.getSource().getWorld());
//                        context.getSource().getWorld().getPlayers().forEach(player -> {
//                            state.playerInfo.forEach((id, role) -> {
//                                System.out.println("玩家：" + player.getName().getString() + " 角色：" + role);
//                            });
//                        });
                        // 判断玩家是不是超级管理员
                        ServerPlayerEntity player = context.getSource().getPlayer();


//                        if (player != null){
//                            if(!context.getSource().getPlayer().hasPermissionLevel(4)){
//                                System.out.println("玩家没有权限");
//                                return 0;
//                            }
//                        }

                        System.out.println("玩家有权限");
                        String[] roles =  {RoleEnum.OFFICIAL.toString(), RoleEnum.PARTICIPANT.toString()};
                        PlayerInfoData playerInfoData = PlayerInfoData.get(context.getSource().getWorld());
                        playerInfoData.playerInfo.forEach((uuid, playerInfo) -> {
                            playerInfo.role = roles[new Random().nextInt(roles.length)];
                        });
                        context.getSource().getWorld().getPersistentStateManager().save();
                        return 0;
                    }));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("addNBTData").executes(context -> {
                        ServerWorld world = context.getSource().getWorld();
                        BriberyTaskInfoData briberyTaskInfoData = BriberyTaskInfoData.get(world);

                        ServerPlayerEntity player = context.getSource().getPlayer();
                        assert player != null;
                            world.getPersistentStateManager().save();
                        return Command.SINGLE_SUCCESS;
                    }));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("bribery")
                            .then(CommandManager.argument("str", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 示例建议：提供几个示例 UUID 或关键词
                                        return CommandSource.suggestMatching(new String[]{
                                                "123e4567-e89b-12d3-a456-426614174000",
                                                "abcd1234-12ab-34cd-ef56-7890abcd1234"
                                        }, builder);
                                    })
                            .executes(
                                    context -> {
                                        ServerWorld world = context.getSource().getWorld();
                                        String str = StringArgumentType.getString(context, "str");
                                        ServerPlayerEntity applicantPlayer = world.getServer().getPlayerManager().getPlayer(UUID.fromString(str));

                                        if(applicantPlayer != null){
                                            applicantPlayer.giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT));
                                            System.out.println("@@@@@@@@@@@@@@@@@@@@@ 给予卡");
                                        }




                                        return 1;
                                    }
                            )));
        });

        // 通过uuid获取世界的玩家



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("myxp")
                            .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                    .then(CommandManager.argument("target", EntityArgumentType.player())
                                            .then(CommandManager.argument("command", StringArgumentType.string()).suggests((context, builder) -> {
                                                                // 提供 "add" 和 "remove" 自动补全
                                                                return CommandSource.suggestMatching(new String[]{"add", "remove"}, builder);
                                                            })
                                                            .executes(context -> {
                                                                int amount = IntegerArgumentType.getInteger(context, "amount");
                                                                String command = StringArgumentType.getString(context, "command");
                                                                System.out.println("amount: " + amount);
                                                                System.out.println("command:" + command);
                                                                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");

                                                                System.out.println("target: " + target.getName().getString());

                                                                for (int i = 0; i < target.getInventory().size(); i++) {
                                                                    ItemStack itemStack = target.getInventory().getStack(i);
                                                                    if (!itemStack.isEmpty()) {
                                                                        String itemName = itemStack.getItem().getName().getString();
                                                                        int itemCount = itemStack.getCount();
                                                                        Identifier itemId = Registries.ITEM.getId(itemStack.getItem());
                                                                        int finalI = i;
                                                                        context.getSource().sendFeedback(() ->
                                                                                Text.literal("背包槽位 " + finalI + ": " + itemName + " x" +
                                                                                        itemCount + "物品id:" + itemId), false);

                                                                        if ("minecraft:dirt".equals(itemId.toString())) {
                                                                            // 减少2个
                                                                            itemStack.setCount(itemStack.getCount() - 2);
                                                                        }
                                                                    }
                                                                }


                                                                if ("add".equalsIgnoreCase(command)) {
                                                                    target.addExperience(amount);
                                                                    context.getSource().sendFeedback(() -> Text.literal("✅ 已向玩家 §6" + target.getName().getString() + " §a增加 §e" + amount + " §a点经验"), false);
                                                                    return 1;
                                                                } else if ("remove".equalsIgnoreCase(command)) {
                                                                    target.addExperience(-amount);
                                                                    context.getSource().sendFeedback(() -> Text.literal("✅ 已从玩家 §6" + target.getName().getString() + " §c扣除 §e" + amount + " §c点经验"), false);
                                                                    return 1;
                                                                } else {
                                                                    context.getSource().sendError(Text.literal("❌ 未知操作: " + command));
                                                                    return -1;
                                                                }
                                                            })
                                            )
                                    )
                            )
            );
        });

        // 注册网络隧道
        ServerNetworkRegister.serverRegister();
        // 初始化
        ModItems.registerItems();
        ModItemGroups.registerModItemGroups();
        ModBlocks.registerModBlocks();

    }

}