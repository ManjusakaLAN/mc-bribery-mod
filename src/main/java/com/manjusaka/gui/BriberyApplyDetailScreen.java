package com.manjusaka.gui;

import com.google.gson.Gson;
import com.manjusaka.constants.CoreConstant;
import com.manjusaka.constants.TreeBlockEnum;
import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.network.NetworkingBindingID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

public class BriberyApplyDetailScreen extends Screen {
    public Screen parent;
    // 总共可以贿赂的数量
    int briberyMaxNum;
    // 当前选择贿赂的数量
    int briberyNum;
    // 每次增加减少 贿赂数量的步长
    private static final int briberyNumStep = CoreConstant.briberyNumStep;
    String officialUuid;

    protected BriberyApplyDetailScreen(Text title, Screen parent,String officialUuid) {
        super(title);
        this.parent = parent;
        this.briberyNum = 0;
        this.officialUuid = officialUuid;
    }

    @Override
    public void close() {
        super.close();
        assert this.client != null;
        this.client.setScreen(parent);

    }

    @Override
    protected void init() {
        // 计算 最大可以贿赂的数量
        if (MinecraftClient.getInstance().player != null) {
            PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();

            List<String> allTreeBlocks = TreeBlockEnum.getAllTreeBlocks();
            briberyMaxNum = 0;
            for (int i = 0; i < 36; i++) {
                // 例如：获取第0个槽位的物品（热键栏第一个）
                ItemStack itemStack = inventory.getStack(i);

                if (!itemStack.isEmpty()) {
//                    String itemName = itemStack.getItem().getName().getString();
                    int itemCount = itemStack.getCount();
//                    System.out.println("槽位" + i + "物品名称：" + itemName + " 数量：" + itemCount);

                    if (allTreeBlocks.contains(Registries.ITEM.getId(itemStack.getItem()).toString())) {
                        briberyMaxNum += itemCount;
                    }
                }
            }
        }

        GridWidget gridWidget = new GridWidget();

        gridWidget.setPosition(120, 70);
        gridWidget.setSpacing(10);

        ButtonWidget buttonWidgetAgree = ButtonWidget.builder(Text.of("⭕"), (btn) -> {



            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
//                int decreaseTreeBlockNum = briberyNum;
//                PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
//                List<String> allTreeBlocks = TreeBlockEnum.getAllTreeBlocks();
//                for (int i = 0; i < 36; i++) {
//                    ItemStack itemStack = inventory.getStack(i);
//                    if (!itemStack.isEmpty()) {
//                        String itemName = itemStack.getItem().getName().getString();
//                        int itemCount = itemStack.getCount();
//                        if (allTreeBlocks.contains(Registries.ITEM.getId(itemStack.getItem()).toString())) {
//                            // 数量小于需要减少的数量
//                            if (decreaseTreeBlockNum <= itemCount) {
//                                itemStack.decrement(decreaseTreeBlockNum);
//                            } else {
//                                itemStack.decrement(itemCount);
//                                decreaseTreeBlockNum -= itemCount;
//                            }
//                        }
//                    }
//                    if (decreaseTreeBlockNum == 0) {
//                        break;
//                    }
//                }

                BriberyTaskInfo briberyTaskInfo = new BriberyTaskInfo(UUID.randomUUID().toString(), player.getUuidAsString(),  player.getEntityName(),briberyNum, this.officialUuid);
                PacketByteBuf buf = PacketByteBufs.create();
                Gson gson = new Gson();
                buf.writeString(gson.toJson(briberyTaskInfo));
                ClientPlayNetworking.send(NetworkingBindingID.REQUEST_BRIBERY_APPLY_ID,buf);
            }
            MinecraftClient.getInstance().execute(this::close);
        }).dimensions(0, 0, 70, 20).build();

        ButtonWidget buttonWidgetRefuse = ButtonWidget.builder(Text.of("❌"), (btn) -> {
            assert this.client != null;
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.UNSECURE_SERVER_WARNING, Text.of("❌"), Text.of("操作取消"))
            );
            MinecraftClient.getInstance().execute(this::close);
        }).dimensions(0, 0, 70, 20).build();

        ButtonWidget buttonWidgetAdd = ButtonWidget.builder(Text.of("+" + briberyNumStep), (btn) -> {
            // 不超过最大贿赂数量
            System.out.println("MAX" + briberyMaxNum);
            if ((briberyNum + briberyNumStep) <= briberyMaxNum) {
                briberyNum += briberyNumStep;
            }

            this.clearAndInit();
        }).dimensions(0, 0, 70, 20).build();

        ButtonWidget buttonWidgetReduce = ButtonWidget.builder(Text.of("-" + briberyNumStep), (btn) -> {
            if ((briberyNum - briberyNumStep) < 0) {
                briberyNum = 0;
            } else {
                briberyNum -= briberyNumStep;
            }
            this.clearAndInit();
        }).dimensions(0, 0, 70, 20).build();


        // 第一行文本
        TextWidget textWidgetLine1 = new TextWidget(
                0, 0, 100, 20,
                Text.literal("请选择贿赂数量(50为一个单位),最大可选择数量:" + (briberyMaxNum / briberyNumStep * briberyNumStep)),
                this.textRenderer
        );
        textWidgetLine1.alignLeft();

// 第二行文本
        TextWidget textWidgetLine2 = new TextWidget(
                0, 0, 100, 20, // Y 坐标增加 20，避免重叠
                Text.literal("此次选择提交的数量:" ),
                this.textRenderer
        );
        textWidgetLine2.alignLeft();

        TextWidget textWidgetLine3 = new TextWidget(
                0, 0, 100, 20, // Y 坐标增加 20，避免重叠
                Text.literal( briberyNum + ""),
                this.textRenderer
        );
        textWidgetLine3.alignLeft();
        textWidgetLine3.setTextColor(0xc94f4f);


        gridWidget.add(textWidgetLine1, 0, 0);
        gridWidget.add(textWidgetLine2, 1, 0);
        gridWidget.add(textWidgetLine3, 1, 1);
        gridWidget.add(buttonWidgetAdd, 2, 0);
        gridWidget.add(buttonWidgetReduce, 2, 1);
        gridWidget.add(buttonWidgetAgree, 3, 0);
        gridWidget.add(buttonWidgetRefuse, 3, 1);


        this.addDrawableChild(buttonWidgetAgree);
        this.addDrawableChild(buttonWidgetRefuse);
        this.addDrawableChild(buttonWidgetReduce);
        this.addDrawableChild(buttonWidgetAdd);
        this.addDrawableChild(textWidgetLine1);
        this.addDrawableChild(textWidgetLine2);
        this.addDrawableChild(textWidgetLine3);

        gridWidget.refreshPositions();
    }
}
