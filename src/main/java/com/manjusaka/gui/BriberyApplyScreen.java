package com.manjusaka.gui;

import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.network.ClientNetworkRegister;
import com.manjusaka.network.NetworkingBindingID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BriberyApplyScreen extends Screen {

    private final List<PlayerInfo> officials;

    public BriberyApplyScreen(Text title, List<PlayerInfo> officials) {
        super(title);
        this.officials = officials;
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

        int count = 0;
        int column = 0;
        int row = 0;
        for (PlayerInfo official : officials) {
            ButtonWidget buttonWidgetAgree = ButtonWidget.builder(Text.of(official.getName()), (btn) -> {
                // When the button is clicked, we can display a toast to the screen.
                assert this.client != null;
                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().setScreen(new BriberyApplyDetailScreen((Text.literal("check box")), this, official.uuid));
                });
            }).dimensions(0, 0, 100, 30).build();
            this.addDrawableChild(buttonWidgetAgree);

            gridWidget.add(buttonWidgetAgree, row, column);
            count++;
            if (count % 3 == 0) {
                column = 0;
                row++;
            } else {
                column++;
            }
        }

        gridWidget.refreshPositions();
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "贿赂官员选择", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }
}
