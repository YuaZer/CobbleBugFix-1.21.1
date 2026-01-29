package io.github.yuazer.cobblebugfix.commands

import io.github.yuazer.cobblebugfix.config.CobbleBugFixConfig
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object ReloadConfigCommand {
    private const val COMMAND_NAME = "cbfreloadconfig"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal(COMMAND_NAME)
                .requires { it.hasPermission(2) }
                .executes { ctx ->
                    CobbleBugFixConfig.load()
                    ctx.source.sendSuccess(
                        { Component.literal("CobbleBugFix 配置已重新加载，当前世界列表：${CobbleBugFixConfig.getConfiguredWorlds()}") },
                        true
                    )
                    1
                }
        )
    }
}
