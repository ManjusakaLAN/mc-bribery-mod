package com.manjusaka.gui;

import com.manjusaka.datapersist.model.BriberyTaskInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BriberyTaskScreen extends Screen {

    public final List<BriberyTaskInfo> briberyTaskInfoList;

    public BriberyTaskScreen(Text title, List<BriberyTaskInfo> briberyTaskInfoList) {
        super(title);
        this.briberyTaskInfoList = briberyTaskInfoList;
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    protected void init() {

        GridWidget gridWidget = new GridWidget();
        gridWidget.setPosition(40, 40);
        gridWidget.setSpacing(10);

        int i = 0;
        for (BriberyTaskInfo briberyTaskInfo :briberyTaskInfoList) {

            ButtonWidget buttonWidgetAgree = ButtonWidget.builder(Text.of("Agree"), (btn) -> {
                // When the button is clicked, we can display a toast to the screen.
                assert this.client != null;
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().setScreen(new CheckBoxScreen((Text.literal("check box")), this, briberyTaskInfo));
                });
            }).dimensions(0, 0, 40, 20).build();
            ButtonWidget buttonWidgetRefuse = ButtonWidget.builder(Text.of("Refuse"), (btn) -> {
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().execute(() -> {
                        MinecraftClient.getInstance().setScreen(new CheckBoxScreen((Text.literal("check box")), this,briberyTaskInfo));
                    });
                });
            }).dimensions(0, 0, 40, 20).build();
            TextWidget  textWidget = new TextWidget(0, 0, 200, 20,
                    Text.of("用户:"+ briberyTaskInfo.applicantName+
                            "向你发起了贿赂申请!数量:" + briberyTaskInfo.briberyNum), this.textRenderer);

            gridWidget.add(textWidget, i, 0);
            gridWidget.add(buttonWidgetAgree, i, 1);
            gridWidget.add(buttonWidgetRefuse, i, 2);
            this.addDrawableChild(textWidget);
            this.addDrawableChild(buttonWidgetAgree);
            this.addDrawableChild(buttonWidgetRefuse);
            i++;
        }

        gridWidget.refreshPositions();
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "任务清单", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }
}