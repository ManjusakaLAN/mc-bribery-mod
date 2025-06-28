package com.manjusaka.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class EventRegister {

    private static final Logger log = LoggerFactory.getLogger(EventRegister.class);

    public static void registerAllEvents()
    {
        // 注册玩家加入世界的时间
        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
        log.info("Register --- 注册玩家加入世界事件成功");
        // 测试使用 玩家获取木头命令
        CommandRegister.woodGivenCommandRegister();
        // 世界参数设定指令
        CommandRegister.worldPropertiesCommandRegister();
        // 身份官员分配指令
        CommandRegister.playerOfficialAssignCommandRegister();

    }

}
