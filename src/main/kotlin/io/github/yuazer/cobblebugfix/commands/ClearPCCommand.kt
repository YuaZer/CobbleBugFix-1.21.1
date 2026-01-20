package io.github.yuazer.cobblebugfix.commands

import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer


object ClearPCCommand {

    private const val COMMAND_NAME = "cbfclearpc"

    /**
     * 供 Mod 主类调用
     */
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal(COMMAND_NAME)
                // OP 权限（2），如需所有人可用可改为 0 或删除
                .requires { it.hasPermission(2) }
                .then(
                    Commands.argument("player", EntityArgument.player())
                        .executes { ctx ->
                            val target = EntityArgument.getPlayer(ctx, "player")
                            val success = clearPC(target)

                            if (success) {
                                ctx.source.sendSuccess(
                                    { Component.literal("已成功清空玩家 ${target.scoreboardName} 的宝可梦PC。") },
                                    true
                                )
                            } else {
                                ctx.source.sendFailure(
                                    Component.literal("无法清空玩家 ${target.scoreboardName} 的PC（PC不存在）。")
                                )
                            }
                            1
                        }
                )
        )
    }

    /**
     * 核心逻辑：清空玩家 PC
     * @return 是否成功
     */
    private fun clearPC(player: ServerPlayer): Boolean {
        val pc = player.party().getOverflowPC(player.registryAccess()) ?: return false
        pc.clearPC()
        return true
    }
}
