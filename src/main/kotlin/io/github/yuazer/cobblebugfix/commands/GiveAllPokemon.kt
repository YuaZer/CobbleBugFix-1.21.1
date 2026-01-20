package io.github.yuazer.cobblebugfix.commands


import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.util.party
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer


object GiveAllPokemon {
    private const val COMMAND_NAME = "cbfgiveallpokemon"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal(COMMAND_NAME)
                // 2=OP；如需所有人可用可改为 0 或移除 requires
                .requires { it.hasPermission(2) }
                .then(
                    Commands.argument("player", EntityArgument.player())
                        .executes { ctx ->
                            val target = EntityArgument.getPlayer(ctx, "player")
                            val count = giveAllPokemon(target)

                            ctx.source.sendSuccess(
                                { Component.literal("已向玩家 ${target.scoreboardName} 的PC发放全部宝可梦，共 $count 只。") },
                                true
                            )
                            1
                        }
                )
        )
    }

    /**
     * 核心逻辑：将所有已实现的宝可梦物种按全国图鉴顺序生成并放入玩家PC（溢出PC）
     * @return 实际添加数量（若PC不可用则为 0）
     */
    private fun giveAllPokemon(player: ServerPlayer): Int {
        val pc = player.party().getOverflowPC(player.registryAccess()) ?: return 0
        val orderedSpecies = PokemonSpecies.implemented.sortedBy { it.nationalPokedexNumber }

        var count = 0
        for (species in orderedSpecies) {
            val pokemon = species.create()
            val displayName = pokemon.getDisplayName(false).string
            if (displayName.contains("cobblemon")){
                println("宝可梦 ${pokemon.species.name} 含有cobblemon，没有汉化，请自行汉化")
            }
            pokemon.setOriginalTrainer(player.uuid)
            pc.add(pokemon)
            count++
        }
        return count
    }
}