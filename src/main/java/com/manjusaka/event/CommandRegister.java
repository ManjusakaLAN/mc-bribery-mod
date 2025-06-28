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

                        ServerPlayerEntity player = context.getSource().getPlayer();
                        assert player != null;
                        player.giveItemStack(new ItemStack(Items.OAK_LOG,100));
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
                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);

                                                if(isPermit){
                                                    ServerWorld world = context.getSource().getWorld();
                                                    int stage = IntegerArgumentType.getInteger(context, "stage");
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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("set_rate")
                            .then(CommandManager.argument("rate", IntegerArgumentType.integer())
                                    .executes(
                                            context -> {

                                                Boolean isPermit = PermissionUtil.checkUserPermission(context,4);
                                                if(isPermit){
                                                    ServerWorld world = context.getSource().getWorld();
                                                    int rate = IntegerArgumentType.getInteger(context, "rate");

                                                    if(rate < 0 || rate > 100){
                                                        context.getSource().sendFeedback(() -> Text.literal("贿赂系数可选区间为:0 - 100").formatted(Formatting.RED), true);
                                                        return 0;
                                                    }

                                                    WorldProperties worldProperties = WorldProperties.get(world);
                                                    worldProperties.setBriberySuccessfulRate(rate);
                                                    context.getSource().sendFeedback(() -> Text.literal("设置贿赂系数为: " +rate), true);
                                                    world.getPersistentStateManager().save();
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
//                                                context.getSource().getServer().getPlayerManager().broadcast(
//                                                        Text.literal("这是一个测试广播消息！"),
//                                                        false // 指定是否隐藏在聊天栏中（true 表示隐藏）
//                                                );
                                                    context.getSource().sendFeedback(() -> Text.literal("设置实验阶段为: " + worldProperties.getStage() + "贿赂系数:" + worldProperties.getBriberySuccessfulRate()), false);

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
                        ServerWorld world = context.getSource().getWorld();
                        PlayerInfoData playerInfoData = PlayerInfoData.get(world);
                        WorldProperties worldProperties = WorldProperties.get(world);

                        // 分配十个官员
                        AtomicInteger assignOfficialNum = new AtomicInteger(CoreConstant.officialNum);
                        assignOfficials(playerInfoData.playerInfo, worldProperties.assignThreshold,assignOfficialNum);
                        // 一个官员都没分配 那么需要提高 分配阈值
                        if(assignOfficialNum.get() == CoreConstant.officialNum){
                            worldProperties.assignThreshold += 1;
                            worldProperties.setAssignThreshold(worldProperties.assignThreshold);
                            // 第二轮分配
                            assignOfficials(playerInfoData.playerInfo, worldProperties.assignThreshold,assignOfficialNum);
                        }

                        playerInfoData.setDirty(true);

                        context.getSource().getWorld().getPersistentStateManager().save();

                        log.info("分配官员完毕！{} 第几 {}", assignOfficialNum.get(), worldProperties.assignThreshold);
                        return 0;
                    }));
        });

        // 重制玩家官员标记
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("clear_official_tag").executes(context -> {
                        // 判断执行权限
                        Boolean isPermit = PermissionUtil.checkUserPermission(context, 4);
                        PlayerInfoData playerInfoData = PlayerInfoData.get(context.getSource().getWorld());
                        playerInfoData.playerInfo.forEach((uuid, playerInfo) -> {
                           playerInfo.usedToBeOfficial = 0;
                        });
                        context.getSource().getWorld().getPersistentStateManager().save();
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
