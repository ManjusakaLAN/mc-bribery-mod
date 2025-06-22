package com.manjusaka.gui;

import com.manjusaka.datapersist.model.BriberyTaskInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
@Environment(EnvType.CLIENT)
public class CheckBoxScreen extends Screen {
    public Screen parent;
    public BriberyTaskInfo task;
    protected CheckBoxScreen(Text title, Screen parent, BriberyTaskInfo  task) {
        super(title);
        this.parent = parent;
        this.task = task;

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
            // 点击的是第几个按钮
            MinecraftClient.getInstance().execute(this::close);
        }).dimensions(0, 0, 40, 20).build();
          ButtonWidget buttonWidgetRefuse = ButtonWidget.builder(Text.of("❌"), (btn) -> {
            // When the button is clicked, we can display a toast to the screen.
            assert this.client != null;
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.UNSECURE_SERVER_WARNING, Text.of("❌"), Text.of("操作取消"))
            );
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
