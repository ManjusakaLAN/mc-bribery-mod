package com.manjusaka.event;

import com.manjusaka.constants.CoreConstant;
import com.manjusaka.constants.RoleEnum;
import com.manjusaka.datapersist.PlayerInfoData;
import com.manjusaka.datapersist.WorldProperties;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.util.PermissionUtil;
import com.manjusaka.util.StageChangeUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandRegister {
    private static final Logger log = LoggerFactory.getLogger(CommandRegister.class);

    public static void woodGivenCommandRegister() {

        /*
         * 命令注册 玩家获取木头u
         */
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("getWood").executes(context -> {
                        ServerWorld world = context.getSource().getWorld();
                        WorldProperties worldProperties = WorldProperties.get(world);
                        if(worldProperties.woodCmdPermit > 0){

                            ServerPlayerEntity player = context.getSource().getPlayer();
                            assert player != null;
                            player.giveItemStack(new ItemStack(Items.OAK_LOG,100));
                        }else {
                            context.getSource().sendFeedback(() -> Text.literal("不允许使用改指令获取木头,该指令仅测试使用").formatted(Formatting.RED), false);
                        }

                        return Command.SINGLE_SUCCESS;
                    }));
        });

    }

    public static void worldPropertiesCommandRegister() {
        // 设置世界 阶段 默认1 可以设置2、3
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("set_stage")
                            .then(CommandManager.argument("stage", IntegerArgumentType.integer())
                                    .suggests((context, builder) -> {
                                        // 示例建议：提供几个示例 UUID 或关键词
                                        return CommandSource.suggestMatching(new String[]{
                                                "2",
                                                "3"
                                        }, builder);
                                    })
                                    .executes(
                                            context -> {
                                                boolean isPermit = PermissionUtil.checkUserPermission(context,4);
                                                int stage = IntegerArgumentType.getInteger(context, "stage");
                                                if(stage < 1 || stage > 3){
                                                    context.getSource().sendFeedback(() -> Text.literal("实验阶段可选区间为:1 - 3").formatted(Formatting.RED), true);
                                                    isPermit = false;
                                                }

                                                if(isPermit){
                                                    ServerWorld world = context.getSource().getWorld();

                                                    WorldProperties worldProperties = WorldProperties.get(world);
                                                    worldProperties.setStage(stage);
                                                    context.getSource().sendFeedback(() -> Text.literal("设置实验阶段为: " + stage), false);

                                                    PlayerInfoData playerInfoData = PlayerInfoData.get(world);
                                                    playerInfoData.playerInfo.forEach((uuid, playerInfo) -> {
                                                        // 获取·服务器用户对象
                                                        ServerPlayerEntity playerOnline = context.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                                        if (playerOnline != null){
                                                            // 修改玩家名称显示  添加称号
                                                            StageChangeUtil.stageChangePlayerNameChange(stage, playerOnline, playerInfo, playerInfo.name);
                                                        }
                                                    });
                                                    world.getPersistentStateManager().save();
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            }
                                    )));
        });
        // 设置贿赂成功率
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("set_rate")
                            .then(CommandManager.argument("rate", IntegerArgumentType.integer())
                                    .executes(
                                            context -> {

                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);
                                                int rate = IntegerArgumentType.getInteger(context, "rate");

                                                if(rate < 0 || rate > 100){
                                                    context.getSource().sendFeedback(() -> Text.literal("贿赂系数可选区间为:0 - 100").formatted(Formatting.RED), true);
                                                    isPermit = false;
                                                }
                                                if(isPermit){
                                                    ServerWorld world = context.getSource().getWorld();


                                                    WorldProperties worldProperties = WorldProperties.get(world);
                                                    worldProperties.setBriberySuccessfulRate(rate);
                                                    context.getSource().sendFeedback(() -> Text.literal("设置贿赂系数为: " +rate), true);
                                                    world.getPersistentStateManager().save();
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            }
                                    )));
        });
        // 设置官员分配数量
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("set_official_num")
                            .then(CommandManager.argument("num", IntegerArgumentType.integer())
                                    .executes(
                                            context -> {
                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);
                                                if(isPermit){
                                                    ServerWorld world = context.getSource().getWorld();
                                                    int num = IntegerArgumentType.getInteger(context, "num");
                                                    WorldProperties worldProperties = WorldProperties.get(world);
                                                    worldProperties.setAssignNum(num);
                                                    context.getSource().sendFeedback(() -> Text.literal("设置官员分配数量为: " +num), true);
                                                    world.getPersistentStateManager().save();
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            }
                                    )));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("set_wood_cmd_permit")
                            .then(CommandManager.argument("num", IntegerArgumentType.integer())
                                    .suggests((context, builder) -> {
                                        // 示例建议：提供几个示例 UUID 或关键词
                                        return CommandSource.suggestMatching(new String[]{
                                                "0",
                                                "1"
                                        }, builder);
                                    })
                                    .executes(
                                            context -> {
                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);
                                                if(isPermit){
                                                    ServerWorld world = context.getSource().getWorld();
                                                    int num = IntegerArgumentType.getInteger(context, "num");
                                                    WorldProperties worldProperties = WorldProperties.get(world);
                                                    worldProperties.setWoodCmdPermit(num);
                                                    context.getSource().sendFeedback(() -> Text.literal("已设置玩家" + (num > 0 ?  "可": "不可") + "使用命令获取模块" ), true);
                                                    world.getPersistentStateManager().save();
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            }
                                    )));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("board_cast")
                            .then(CommandManager.argument("message", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // 示例建议：提供几个示例 UUID 或关键词
                                        return CommandSource.suggestMatching(new String[]{
                                                ""
                                        }, builder);
                                    })

                                    .executes(
                                            context -> {
                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);

                                                if(isPermit){
                                                    System.out.println("@@@@@@");
                                                    String message = StringArgumentType.getString(context, "message");
                                                    System.out.println("@@@@" +  message);
                                                    context.getSource().getServer().getPlayerManager().broadcast(
                                                        Text.literal(message).formatted(Formatting.GOLD),
                                                        false // 指定是否隐藏在聊天栏中（true 表示隐藏）
                                                );
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            }
                                    )));
        });

        // 获取世界实验参数
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("world_info")
                                    .executes(
                                            context -> {

                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);
                                                if (isPermit){
                                                    ServerWorld world = context.getSource().getWorld();
                                                    WorldProperties worldProperties = WorldProperties.get(world);
                                                    context.getSource().sendFeedback(() -> Text.literal("设置实验阶段为: " + worldProperties.getStage() +
                                                            " 贿赂系数:" + worldProperties.getBriberySuccessfulRate() +
                                                            " 分配阈值:" + worldProperties.assignThreshold +
                                                            " 官员分配数量:" + worldProperties.assignNum +
                                                            "是否允许玩家通过指令获取木头:" + worldProperties.woodCmdPermit).formatted(Formatting.BLUE), false);

                                                }

                                                     return Command.SINGLE_SUCCESS;
                                            }
                                    ));
        });
    }

    public static void playerOfficialAssignCommandRegister() {
        // 分配10个官员  必须是没有当过官员的玩家
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("assign_official").executes(context -> {
                        // 判断执行权限
                        Boolean isPermit = PermissionUtil.checkUserPermission(context, 4);
                        if(isPermit){
                            ServerWorld world = context.getSource().getWorld();
                            PlayerInfoData playerInfoData = PlayerInfoData.get(world);
                            WorldProperties worldProperties = WorldProperties.get(world);
                            log.info("分配官员开始 数量{}", worldProperties.assignNum);
                            // 分配官员数量
                            AtomicInteger assignOfficialNum = new AtomicInteger(worldProperties.assignNum);
                            assignOfficials(playerInfoData.playerInfo, worldProperties.assignThreshold,assignOfficialNum);
                            // 一个官员都没分配 那么需要提高 分配阈值
                            if(assignOfficialNum.get() == worldProperties.assignNum){
                                worldProperties.assignThreshold += 1;
                                worldProperties.setAssignThreshold(worldProperties.assignThreshold);
                                // 第二轮分配
                                assignOfficials(playerInfoData.playerInfo, worldProperties.assignThreshold,assignOfficialNum);
                            }

                            playerInfoData.setDirty(true);

                            context.getSource().getWorld().getPersistentStateManager().save();

                        }
                        return 0;
                    }));
        });

        // 重制玩家官员标记
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("clear_official_tag").executes(context -> {
                        // 判断执行权限
                        Boolean isPermit = PermissionUtil.checkUserPermission(context, 4);
                        if(isPermit){
                            PlayerInfoData playerInfoData = PlayerInfoData.get(context.getSource().getWorld());
                            playerInfoData.playerInfo.forEach((uuid, playerInfo) -> {
                                playerInfo.usedToBeOfficial = 0;
                            });
                            context.getSource().getWorld().getPersistentStateManager().save();
                        }
                        return 0;
                    }));

        });

    }

    /**
     * @param playerInfos
     * @param assignThreshold
     * @param assignOfficialNum
     */
    private static void assignOfficials(Map<UUID, PlayerInfo> playerInfos, int assignThreshold, AtomicInteger assignOfficialNum) {
        playerInfos.forEach((uuid, playerInfo) -> {
            //分配完数量直接退出
            if (assignOfficialNum.get() <= 0){
                playerInfo.role = RoleEnum.PARTICIPANT.name();
                log.info("玩家:{} 分配 Participant 成功！{}", playerInfo.name, assignOfficialNum.get());
                return;
            }
            // 必须小于分配阈值  阈值代表这是第几轮分配
            if(playerInfo.usedToBeOfficial < assignThreshold){
                playerInfo.role = RoleEnum.OFFICIAL.name();
                assignOfficialNum.getAndDecrement();
                playerInfo.usedToBeOfficial += 1;
                log.info("玩家:{} 分配 Official 成功！", playerInfo.name);
            }else {
                playerInfo.role = RoleEnum.PARTICIPANT.name();
            }
        });
    }
}
