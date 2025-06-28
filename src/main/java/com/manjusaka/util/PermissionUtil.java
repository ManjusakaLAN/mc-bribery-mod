package com.manjusaka.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PermissionUtil {


    public static Boolean checkUserPermission(CommandContext<ServerCommandSource> context,int permitLevel) {
        ServerPlayerEntity player =  context.getSource().getPlayer();
        if (player != null){
            if(context.getSource().getPlayer().hasPermissionLevel(permitLevel)){
                return true;
            }
        }
        context.getSource().sendFeedback(() -> Text.literal("您没有权限发送改指令").formatted(Formatting.RED), false);
        return false;
    }
}
