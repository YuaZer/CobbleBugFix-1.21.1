package io.github.yuazer.cobblebugfix.commands

import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import io.github.yuazer.cobblebugfix.util.PokemonUtil
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component

object ForceTradeEvolutionCommand {

    private const val COMMAND_NAME = "cbfforcetradeevo"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal(COMMAND_NAME)
                // 2=OP权限，如需所有人可用可改为 0 或移除 requires
                .requires { it.hasPermission(2) }
                .then(
                    Commands.argument("player", EntityArgument.player())
                        .then(
                            Commands.argument("slot", IntegerArgumentType.integer(1, 6))
                                .then(
                                    Commands.argument("evolutionId", StringArgumentType.greedyString())
                                        .executes { ctx ->
                                            val target = EntityArgument.getPlayer(ctx, "player")
                                            val slot = IntegerArgumentType.getInteger(ctx, "slot")
                                            val evolutionId = StringArgumentType.getString(ctx, "evolutionId")

                                            val pokemon = target.party().get(slot - 1)
                                            if (pokemon == null) {
                                                ctx.source.sendFailure(
                                                    Component.literal("槽位 $slot 为空，无法强制进化。")
                                                )
                                                return@executes 0
                                            }

                                            val success = PokemonUtil.forceTradeEvolution(pokemon, evolutionId)
                                            if (success) {
                                                ctx.source.sendSuccess(
                                                    { Component.literal("已为 ${target.scoreboardName} 的槽位 $slot 强制触发交易进化：$evolutionId") },
                                                    true
                                                )
                                                1
                                            } else {
                                                ctx.source.sendFailure(
                                                    Component.literal("未找到匹配的交易进化：$evolutionId")
                                                )
                                                0
                                            }
                                        }
                                )
                        )
                )
        )
    }
}
