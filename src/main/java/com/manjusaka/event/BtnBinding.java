package com.manjusaka.event;

import com.manjusaka.network.NetworkingBindingID;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BtnBinding {

    private static final Logger log = LoggerFactory.getLogger(BtnBinding.class);

    public static  ClientTickEvents.EndTick openBriberyApplyGuiBtnBinding(){

        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.tutorial-mod.open_bribery_apply_gui",
                        GLFW.GLFW_KEY_G,
                        "key.tutorial-mod.apply" ));

        return(event) -> {
                while (keyBinding.wasPressed()) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeString("");
                    ClientPlayNetworking.send(NetworkingBindingID.REQUEST_OFFICIAL_DATA_ID, buf);
                    // 按键按下时触发
                }
        };
    }

    public static  ClientTickEvents.EndTick openBriberyTaskGuiBtnBinding(){

        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.tutorial-mod.open_bribery_task_gui",
                        GLFW.GLFW_KEY_H,
                        "key.tutorial-mod.task" ));

        return(event) -> {
            while (keyBinding.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString("");
                ClientPlayNetworking.send(NetworkingBindingID.REQUEST_BRIBERY_TAK_ID, buf);
            }
        };
    }


}
