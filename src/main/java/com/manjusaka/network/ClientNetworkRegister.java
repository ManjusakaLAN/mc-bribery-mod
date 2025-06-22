package com.manjusaka.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.manjusaka.constants.RoleEnum;
import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.gui.BriberyApplyScreen;
import com.manjusaka.gui.BriberyTaskScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.manjusaka.network.NetworkingBindingID.*;
import static net.minecraft.client.realms.task.LongRunningTask.setScreen;

public class ClientNetworkRegister {

    public static void clientRegister(){
        // 注册官员信息获取网络通道 参与者获取当前世界的官员
        clientOfficialNetRegister();
        // 注册任务获取网络通道 官员拿到可以处理的贿赂申请
        clientBriberyTaskNetRegister();
        // 注册任务申请网络通道   参与者提交贿赂申请
        clientBriberyApplyNetRegister();
        // 注册任务处理网络通道 官员处理任务的提交
        clientBriberyTaskHandleNetRegister();
    }

    private static void clientBriberyApplyNetRegister() {
        // 客户端监听来自服务端的消息
        ClientPlayNetworking.registerGlobalReceiver(SEND_BRIBERY_APPLY_ID, (client, handler, buf, responseSender) -> {
            String data = buf.readString(32767); // 接收字符串数据
            client.execute(() -> {
                // 在客户端主线程中处理数据
                System.out.println("@1收到服务端数据: " + data);
            });
        });
    }

    private static void clientBriberyTaskHandleNetRegister() {
        ClientPlayNetworking.registerGlobalReceiver(SEND_BRIBERY_TASK_HANDLE_ID, (client, handler, buf, responseSender) -> {
            String data = buf.readString(32767); // 接收字符串数据
            client.execute(() -> {
                // 在客户端主线程中处理数据
                System.out.println("@2收到服务端数据: " + data);
            });
        });

    }



    public static void clientOfficialNetRegister() {
        // 客户端监听来自服务端的消息
        ClientPlayNetworking.registerGlobalReceiver(SEND_OFFICIAL_DATA_ID, (client, handler, buf, responseSender) -> {
            String data = buf.readString(32767); // 接收字符串数据
            client.execute(() -> {
                // 使用 TypeToken 获取带泛型的类型
                Type mapType = new TypeToken<Map<String, PlayerInfo>>(){}.getType();
                Map<String, PlayerInfo> playerInfoMap = new Gson().fromJson(data, mapType);
                // 可更新 GUI 或日志等操作
                List<PlayerInfo> officials = new ArrayList<>();
                playerInfoMap.forEach((uuid, info) -> {
                    if(RoleEnum.OFFICIAL.toString().equals(info.getRole())){
                        officials.add(info);
                    }
                });
                setScreen(new BriberyApplyScreen(Text.literal("bribery_task_screen"),officials));
            });
        });
    }

    public static void clientBriberyTaskNetRegister() {
        // 监听来自服务端的消息
        ClientPlayNetworking.registerGlobalReceiver(NetworkingBindingID.SEND_BRIBERY_TASK_ID, (client, handler, buf, responseSender) -> {
            String data = buf.readString(32767); // 接收字符串数据

            Type mapType = new TypeToken<List<BriberyTaskInfo>>(){}.getType();
            List<BriberyTaskInfo> briberyTaskInfoList = new Gson().fromJson(data, mapType);

            setScreen(new BriberyTaskScreen(Text.literal("bribery_task_screen"), briberyTaskInfoList));
        });
    }
}
