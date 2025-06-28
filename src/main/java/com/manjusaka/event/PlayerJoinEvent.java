package com.manjusaka.event;

import com.manjusaka.constants.RoleEnum;
import com.manjusaka.datapersist.PlayerInfoData;
import com.manjusaka.datapersist.WorldProperties;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.item.ModItems;
import com.manjusaka.mixin.PlayerEntityMixin;
import com.manjusaka.util.StageChangeUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerJoinEvent implements ServerPlayConnectionEvents.Join {

    private static final Logger log = LoggerFactory.getLogger(PlayerJoinEvent.class);

    @Override
    public void onPlayReady(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        // 玩家加入服务器时触发
        PlayerInfoData playerInfoData = PlayerInfoData.get(minecraftServer.getOverworld());

        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();

        PlayerInfo playerInfo = playerInfoData.getPlayerRole(player.getUuid());
        if (playerInfo != null) {
            player.sendMessage(Text.of(playerInfo.getName() + "，欢迎回来!您当前的身份是:" + (RoleEnum.OFFICIAL.name().equals(playerInfo.getRole()) ? "官员" : "参与者")));
            int offlinePermits = playerInfo.getOfflinePermits();
            player.sendMessage(Text.of("您离线时获取的合成许可证发放数量:" + offlinePermits));
            if (offlinePermits > 0) {
                serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT, offlinePermits));
            }
            playerInfo.setOfflinePermits(0);
            playerInfoData.updatePlayerRole(player.getUuid(), playerInfo);

        } else {
            player.sendMessage(Text.of("欢迎来到服务器!您当前的身份是:参与者"));
            // 为玩家发一套木制工具
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_AXE));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_PICKAXE));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_SHOVEL));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_HOE));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_SWORD));
            // 发送三个合成通行证
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT,3));
            playerInfo = new PlayerInfo(player.getName().getString(), player.getUuid().toString(), 1, 0, RoleEnum.PARTICIPANT.name());
            playerInfoData.setPlayerRole(player.getUuid(), playerInfo);

        }

        WorldProperties worldProperties = WorldProperties.get(minecraftServer.getOverworld());
        StageChangeUtil.stageChangePlayerNameChange(worldProperties.getStage(), player, playerInfo, player.getName().getString());
        minecraftServer.getOverworld().getPersistentStateManager().save();
    }
}
