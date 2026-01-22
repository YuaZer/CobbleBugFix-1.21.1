package io.github.yuazer.cobblebugfix

import io.github.yuazer.cobblebugfix.commands.ClearPCCommand
import io.github.yuazer.cobblebugfix.commands.ForceTradeEvolutionCommand
import io.github.yuazer.cobblebugfix.commands.GiveAllPokemon
import io.github.yuazer.cobblebugfix.handler.ServerHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.slf4j.LoggerFactory

class Cobblebugfix : ModInitializer {
    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            GiveAllPokemon.register(dispatcher)
            ClearPCCommand.register(dispatcher)
            ForceTradeEvolutionCommand.register(dispatcher)
        })
        ServerHandler.register()
        val logger = LoggerFactory.getLogger("CobbleBugFix")
        logger.info("CobbleBugFix initialized")
    }
}
