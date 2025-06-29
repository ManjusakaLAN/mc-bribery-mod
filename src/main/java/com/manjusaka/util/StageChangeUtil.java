package com.manjusaka.util;

import com.manjusaka.datapersist.model.PlayerInfo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 实验分三个阶段
 * 第一个阶段：所有人没有头衔
 * 第二个阶段：第一阶段中，腐败行为数=0，获得头衔【清风拂面】（浅绿色）
 *                      第一阶段中，0＜腐败行为数＜5，无任何头衔
 *                      第一阶段中，5＜腐败行为数＜15，获得头衔【暗中交易者】（浅红）
 *                      第一阶段中，15＜腐败行为数，获得头衔【贪婪之手】（深红）
 * 第三个阶段：前两个阶段中腐败行为数=0，获得头衔【当代海瑞】（金色）
 *             前两个阶段中，0＜腐败行为数＜5，获得头衔【老实本分】（白色）
 *             前两个阶段中，5＜腐败行为数＜15，获得头衔【贪婪之手】（深红）
 *             前两个阶段中，15＜腐败行为数，获得头衔【当代和珅】（紫色）
 */
public class StageChangeUtil {


    public static void stageChangePlayerNameChange(int stage, ServerPlayerEntity player, PlayerInfo playerInfo, String playerName) {

        if(stage == 1){
            return;
        }else if (stage == 2){
            if(playerInfo.stageOneBriberyTimes == 0){
                player.setCustomName(Text.literal("[清风拂面]" + playerName).formatted(Formatting.GREEN));
            }else if (playerInfo.stageOneBriberyTimes > 15){
                player.setCustomName(Text.literal("[贪婪之手]" +playerName).formatted(Formatting.RED));
            }else {
                player.setCustomName(Text.literal(playerName).formatted(Formatting.WHITE));
            }
        }else {
            if (playerInfo.stageOneBriberyTimes == 0 && playerInfo.stageTwoBriberyTimes == 0){
                player.setCustomName(Text.literal("[当代海瑞]" + playerName).formatted(Formatting.GOLD));
            }
            if(playerInfo.briberyTimes < 5 && playerInfo.briberyTimes > 0){
                player.setCustomName(Text.literal("[老实本分]" +playerName).formatted(Formatting.WHITE));
            }
            if (playerInfo.briberyTimes < 15 && playerInfo.briberyTimes > 5){
                player.setCustomName(Text.literal("[贪婪之手]" + playerName).formatted(Formatting.RED));
            }
            if(playerInfo.briberyTimes > 15){
                player.setCustomName(Text.literal("[当代和珅]" + playerName).formatted(Formatting.BLUE));
            }

        }
    }
}
