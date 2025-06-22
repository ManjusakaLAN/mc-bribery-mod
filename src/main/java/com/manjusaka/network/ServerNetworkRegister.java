package com.manjusaka.network;

import com.google.gson.Gson;
import com.manjusaka.datapersist.BriberyTaskInfoData;
import com.manjusaka.datapersist.PlayerInfoData;
import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.item.ModItems;
import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.manjusaka.network.NetworkingBindingID.*;

public class ServerNetworkRegister {

    public static void serverRegister() {
        serverOfficialNetRegister();
        serverBriberyTaskNetRegister();
        serverBriberyApplyNetRegister();
        serverBriberyTaskHandleNetRegister();

    }

    private static void serverBriberyTaskHandleNetRegister() {
        // 服务端监听客户端请求
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BRIBERY_TASK_HANDLE_ID, (server, player, handler, buf, responseSender) -> {

            String uuid = buf.readString();
            System.out.println("Received request from " + player.getName().getString() + ": " + uuid);
            server.execute(() -> {
                // 获取服务端世界和数据
                ServerWorld world = server.getWorld(World.OVERWORLD);
                ServerPlayerEntity applicantPlayer = server.getPlayerManager().getPlayer(UUID.fromString(uuid));
                player.giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT));
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
                    if (briberyTaskInfo.briberyNum > 0) {
                        BriberyTaskInfoData briberyTaskInfoData = BriberyTaskInfoData.get(world);
                        briberyTaskInfoData.submitTask(UUID.fromString(briberyTaskInfo.officialUuid), briberyTaskInfo);
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
