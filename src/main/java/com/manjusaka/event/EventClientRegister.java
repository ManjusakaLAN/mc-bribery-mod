package com.manjusaka.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class EventClientRegister {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EventClientRegister.class);

    public static void registerAllEvents()
    {
        log.info("Register --- 注册玩家点击按钮事件成功 -- 点击打开申请面板GUI");
        ClientTickEvents.END_CLIENT_TICK.register(BtnBinding.openBriberyApplyGuiBtnBinding());
        log.info("Register --- 注册玩家点击按钮事件成功 -- 点击打开任务面板GUI");
        ClientTickEvents.END_CLIENT_TICK.register(BtnBinding.openBriberyTaskGuiBtnBinding());

    }
}
