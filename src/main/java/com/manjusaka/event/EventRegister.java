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


    }

}
