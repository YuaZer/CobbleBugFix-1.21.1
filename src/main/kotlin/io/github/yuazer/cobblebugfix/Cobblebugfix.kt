package io.github.yuazer.cobblebugfix

import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import io.github.yuazer.cobblebugfix.commands.ClearPCCommand
import io.github.yuazer.cobblebugfix.commands.GiveAllPokemon
import io.github.yuazer.cobblebugfix.properties.LevelRangePropertyType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import org.slf4j.LoggerFactory

class Cobblebugfix : ModInitializer {
    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            GiveAllPokemon.register(dispatcher)
            ClearPCCommand.register(dispatcher)
        })
        CustomPokemonProperty.register(LevelRangePropertyType)

        val logger = LoggerFactory.getLogger("CobbleBugFix")
        logger.info("CobbleBugFix initialized")
    }
}
