package com.manjusaka.network;


import net.minecraft.util.Identifier;


public class NetworkingBindingID {
    // 客户端与服务端 获取官员用户的网络隧道绑定id
    public static final Identifier REQUEST_OFFICIAL_DATA_ID = new Identifier("tutorial-mod", "request_official_data");
    public static final Identifier SEND_OFFICIAL_DATA_ID = new Identifier("tutorial-mod", "send_official__data");

    // 任务请求网络管道绑定id
    public static final Identifier REQUEST_BRIBERY_TAK_ID = new Identifier("tutorial-mod", "request_bribery_task");
    public static final Identifier SEND_BRIBERY_TASK_ID = new Identifier("tutorial-mod", "send_bribery_task");

    // 任务申请网络管道绑定id
    public static final Identifier REQUEST_BRIBERY_APPLY_ID = new Identifier("tutorial-mod", "request_bribery_apply");
    public static final Identifier SEND_BRIBERY_APPLY_ID = new Identifier("tutorial-mod", "send_bribery_apply");

    // 任务处理网络管道绑定id
    public static final Identifier REQUEST_BRIBERY_TASK_HANDLE_ID = new Identifier("tutorial-mod", "request_bribery_task_handle");
    public static final Identifier SEND_BRIBERY_TASK_HANDLE_ID = new Identifier("tutorial-mod", "send_bribery_task_handle");
}
