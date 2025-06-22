package com.manjusaka.event;

import com.manjusaka.datapersist.PlayerInfoData;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerJoinEvent implements ServerPlayConnectionEvents.Join {

    @Override
    public void onPlayReady(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        // 玩家加入服务器时触发
        PlayerInfoData playerInfoData = PlayerInfoData.get(minecraftServer.getOverworld());

        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();

        PlayerInfo playerInfo = playerInfoData.getPlayerRole(player.getUuid());
        if (playerInfo != null) {
            player.sendMessage(Text.of(playerInfo.getName() + "，欢迎回来！"));
            int offlinePermits = playerInfo.getOfflinePermits();
            player.sendMessage(Text.of("您离线时获取的合成许可证发放数量:" + offlinePermits));
            if (offlinePermits > 0) {
                serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT, offlinePermits));
            }
            playerInfo.setOfflinePermits(0);
            playerInfoData.updatePlayerRole(player.getUuid(), playerInfo);

        } else {
            player.sendMessage(Text.of("欢迎来到服务器！"));
            // 为玩家发一套木制工具
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_AXE));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_PICKAXE));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_SHOVEL));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_HOE));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(Items.WOODEN_SWORD));
            serverPlayNetworkHandler.getPlayer().giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT));

            playerInfoData.setPlayerRole(player.getUuid(), new PlayerInfo(player.getName().getString(), player.getUuid().toString(), 1, 0, "OFFICIAL"));
        }
        minecraftServer.getOverworld().getPersistentStateManager().save();
    }
}
