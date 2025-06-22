package com.manjusaka;

import com.manjusaka.event.EventClientRegister;
import com.manjusaka.network.ClientNetworkRegister;
import net.fabricmc.api.ClientModInitializer;

public class ClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EventClientRegister.registerAllEvents();
        // 绑定网络隧道
        ClientNetworkRegister.clientRegister();
    }


}
