package io.github.yuazer.cobblebugfix.handler

import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.platform.events.PlatformEvents
import io.github.yuazer.cobblebugfix.properties.LevelRangePropertyType

object ServerHandler {
    fun register(){
        PlatformEvents.SERVER_STARTED.subscribe {
            registerCustomProperties()
        }
    }
    fun registerCustomProperties() {
        CustomPokemonProperty.register(LevelRangePropertyType)
    }
}