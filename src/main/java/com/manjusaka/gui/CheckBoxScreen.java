package com.manjusaka.gui;

import com.google.gson.Gson;
import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.network.NetworkingBindingID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class CheckBoxScreen extends Screen {
    public Screen parent;
    public BriberyTaskInfo task;
    public Boolean agreeFlag;
    protected CheckBoxScreen(Text title, Screen parent, BriberyTaskInfo  task, Boolean agreeFlag) {
        super(title);
        this.parent = parent;
        this.task = task;
        this.agreeFlag = agreeFlag;

    }

    @Override
    public void close() {
        super.close();
        assert this.client != null;
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.setPosition(120, 70);
        gridWidget.setSpacing(10);

        ButtonWidget buttonWidgetAgree = ButtonWidget.builder(Text.of("⭕"), (btn) -> {
            BriberyTaskScreen briberyTaskScreen =  (BriberyTaskScreen)parent;
            briberyTaskScreen.briberyTaskInfoList.remove(this.task);
            if(agreeFlag){
                // 同意贿赂请求
                task.isAccepted = true;
            }else {
                // 拒绝贿赂请求
                task.isAccepted = false;
            }
            PacketByteBuf buf = PacketByteBufs.create();
            Gson gson = new Gson();
            buf.writeString(gson.toJson(task));
            ClientPlayNetworking.send(NetworkingBindingID.REQUEST_BRIBERY_TASK_HANDLE_ID,buf);
            MinecraftClient.getInstance().execute(this::close);
        }).dimensions(0, 0, 40, 20).build();
          ButtonWidget buttonWidgetRefuse = ButtonWidget.builder(Text.of("❌"), (btn) -> {
              if (this.client != null){
                  this.client.getToastManager().add(
                          SystemToast.create(this.client, SystemToast.Type.UNSECURE_SERVER_WARNING, Text.of("❌"), Text.of("操作取消"))
                  );
              }
            MinecraftClient.getInstance().execute(this::close);
        }).dimensions(0, 0, 40, 20).build();


        TextWidget textWidget = new TextWidget(0, 0, 90, 20, Text.of("选择贿赂数量:"), this.textRenderer);

        gridWidget.add(textWidget, 0, 0);
        gridWidget.add(buttonWidgetAgree, 1, 0);
        gridWidget.add(buttonWidgetRefuse, 1, 1);

        this.addDrawableChild(buttonWidgetAgree);
        this.addDrawableChild(buttonWidgetRefuse);
        this.addDrawableChild(textWidget);

        gridWidget.refreshPositions();
    }
}
