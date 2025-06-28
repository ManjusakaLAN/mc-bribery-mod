package com.manjusaka.network;

import com.google.gson.Gson;
import com.manjusaka.constants.CoreConstant;
import com.manjusaka.constants.TreeBlockEnum;
import com.manjusaka.datapersist.BriberyTaskInfoData;
import com.manjusaka.datapersist.BriberyTaskRecorder;
import com.manjusaka.datapersist.PlayerInfoData;
import com.manjusaka.datapersist.WorldProperties;
import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.datapersist.model.BriberyTaskResultInfo;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.manjusaka.network.NetworkingBindingID.*;

public class ServerNetworkRegister {

    private static final Logger log = LoggerFactory.getLogger(ServerNetworkRegister.class);

    public static void serverRegister() {
        serverOfficialNetRegister();
        serverBriberyTaskNetRegister();
        serverBriberyApplyNetRegister();
        serverBriberyTaskHandleNetRegister();

    }

    private static void serverBriberyTaskHandleNetRegister() {
        // 服务端监听客户端请求
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BRIBERY_TASK_HANDLE_ID, (server, player, handler, buf, responseSender) -> {

            String briberyTask = buf.readString();

            Gson gson = new Gson();
            BriberyTaskInfo briberyTaskInfo = gson.fromJson(briberyTask, BriberyTaskInfo.class);

            BriberyTaskResultInfo taskResult = new BriberyTaskResultInfo();

            server.execute(() -> {
                // 获取服务端世界和数据
                ServerWorld world = server.getWorld(World.OVERWORLD);
                int rate = 0;
                if(world != null){
                   if(briberyTaskInfo.isAccepted){
                       ServerPlayerEntity applicantPlayer = server.getPlayerManager().getPlayer(UUID.fromString(briberyTaskInfo.applicantUuid));
                       int i = new Random().nextInt(100);
                       rate = WorldProperties.get(world).getBriberySuccessfulRate();
                       if(i < rate){// 接受有一定概率成功
                           taskResult.isSuccess = true;
                           if(applicantPlayer != null){
                               applicantPlayer.giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT,briberyTaskInfo.briberyNum / CoreConstant.briberyNumStep));
                           }else {
                               PlayerInfoData playerInfoData = PlayerInfoData.get(world);
                               PlayerInfo playerInfo = playerInfoData.getPlayerRole(UUID.fromString(briberyTaskInfo.applicantUuid));
                               playerInfo.setOfflinePermits(playerInfo.getOfflinePermits() + (briberyTaskInfo.briberyNum / CoreConstant.briberyNumStep));
                               playerInfoData.updatePlayerRole(UUID.fromString(briberyTaskInfo.applicantUuid), playerInfo);
                           }
                       }else {
                           taskResult.isSuccess = false;
                       }
                   }else {
                       // 被拒绝那么此次任务一定失败
                       taskResult.isSuccess = false;
                   }
                   // 记录任务 处理日志信息
                    taskResult.taskUuid = briberyTaskInfo.taskUuid;
                    taskResult.applicantUuid = briberyTaskInfo.applicantUuid;
                    taskResult.applicantName = briberyTaskInfo.applicantName;
                    taskResult.officialUuid = briberyTaskInfo.officialUuid;
                    taskResult.officialName = player.getEntityName();
                    taskResult.briberyNum = briberyTaskInfo.briberyNum;
                    taskResult.isAccepted = briberyTaskInfo.isAccepted;
                    taskResult.rate = rate;
                    taskResult.handlerDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

                    BriberyTaskRecorder briberyTaskRecorder = BriberyTaskRecorder.get(world);
                    briberyTaskRecorder.recordTaskResult(UUID.fromString(briberyTaskInfo.officialUuid), taskResult);
                    BriberyTaskInfoData briberyTaskInfoData = BriberyTaskInfoData.get(world);
                    briberyTaskInfoData.removeTask(UUID.fromString(briberyTaskInfo.officialUuid), briberyTaskInfo);
                    world.getPersistentStateManager().save();
                }

            });
        });
    }

    private static void serverBriberyApplyNetRegister() {
        // 服务端监听客户端请求
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BRIBERY_APPLY_ID, (server, player, handler, buf, responseSender) -> {

            String jsonBriberyTaskInfo = buf.readString();

            server.execute(() -> {
                // 获取服务端世界和数据
                ServerWorld world = server.getWorld(World.OVERWORLD);
                if (world != null) {
                    Gson gson = new Gson();
                    BriberyTaskInfo briberyTaskInfo = gson.fromJson(jsonBriberyTaskInfo, BriberyTaskInfo.class);

                    int decreaseTreeBlockNum = briberyTaskInfo.briberyNum;
                    PlayerInventory inventory = player.getInventory();
                    List<String> allTreeBlocks = TreeBlockEnum.getAllTreeBlocks();
                    for (int i = 0; i < 36; i++) {
                        ItemStack itemStack = inventory.getStack(i);
                        if (!itemStack.isEmpty()) {
                            int itemCount = itemStack.getCount();
                            if (allTreeBlocks.contains(Registries.ITEM.getId(itemStack.getItem()).toString())) {
                                // 当前格子的数量 >= 需要减少的数量
                                if (decreaseTreeBlockNum <= itemCount) {
                                    itemStack.decrement(decreaseTreeBlockNum);
                                    decreaseTreeBlockNum = 0;
                                } else {
                                    // 当前格子的数量 < 需要减少的数量
                                    itemStack.decrement(itemCount);
                                    decreaseTreeBlockNum -= itemCount;
                                }
                            }
                        }
                        if (decreaseTreeBlockNum == 0) {
                            break;
                        }
                    }

                    if (briberyTaskInfo.briberyNum > 0) {

                        PlayerInfoData playerInfoData = PlayerInfoData.get(world);
                        PlayerInfo playerRole = playerInfoData.getPlayerRole(player.getUuid());
                        playerRole.briberyTimes += 1;

                        WorldProperties worldProperties = WorldProperties.get(world);
                        if(1 == worldProperties.getStage()){
                            playerRole.stageOneBriberyTimes +=1;
                        }else if (2 == worldProperties.getStage()){
                            playerRole.stageTwoBriberyTimes +=1;
                        }else {
                            playerRole.stageThreeBriberyTimes +=1;
                        }
                        playerInfoData.updatePlayerRole(player.getUuid(),playerRole);

                        BriberyTaskInfoData briberyTaskInfoData = BriberyTaskInfoData.get(world);
                        briberyTaskInfoData.submitTask(UUID.fromString(briberyTaskInfo.officialUuid), briberyTaskInfo);
                        world.getPersistentStateManager().save();
                    }
                }
            });
        });
    }

    public static void serverOfficialNetRegister() {
        // 服务端监听客户端请求
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_OFFICIAL_DATA_ID, (server, player, handler, buf, responseSender) -> {

            String s = buf.readString();
            System.out.println("Received request from " + player.getName().getString() + ": " + s);

            server.execute(() -> {
                // 获取服务端世界和数据
                ServerWorld world = server.getWorld(World.OVERWORLD);
                if (world != null) {
                    PlayerInfoData playerInfoData = PlayerInfoData.get(world);
                    Gson gson = new Gson();
                    String json = gson.toJson(playerInfoData.playerInfo);

                    // 回复客户端
                    PacketByteBuf responseBuf = PacketByteBufs.create();
                    responseBuf.writeString(json);
                    ServerPlayNetworking.send(player, SEND_OFFICIAL_DATA_ID, responseBuf);
                }
            });
        });
    }

    public static void serverBriberyTaskNetRegister() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkingBindingID.REQUEST_BRIBERY_TAK_ID, (server, player, handler, buf, responseSender) -> {

            String uuid = buf.readString();
            System.out.println("Received request from " + player.getName().getString() + ": " + uuid);


            server.execute(() -> {
                ServerWorld world = server.getWorld(World.OVERWORLD);
                if (world != null) {
                    BriberyTaskInfoData briberyTaskInfoData = BriberyTaskInfoData.get(world);
                    List<BriberyTaskInfo> task = briberyTaskInfoData.getTask(player.getUuid());

                    if (task == null) {
                        task = new CopyOnWriteArrayList<>();
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(task);

                    PacketByteBuf responseBuf = PacketByteBufs.create();
                    responseBuf.writeString(json);
                    ServerPlayNetworking.send(player, SEND_BRIBERY_TASK_ID, responseBuf);
                }
            });
        });
    }
}
