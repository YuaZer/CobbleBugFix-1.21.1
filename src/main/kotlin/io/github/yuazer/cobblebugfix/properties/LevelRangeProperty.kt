package io.github.yuazer.cobblebugfix.properties

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class LevelRangeProperty(private val range: IntRange) : CustomPokemonProperty {
    override fun asString() = "levelRange=${range.first}-${range.last}"

    override fun apply(pokemon: Pokemon) {
        val clamped = range.first.coerceIn(1, Cobblemon.config.maxPokemonLevel)..
                range.last.coerceIn(1, Cobblemon.config.maxPokemonLevel)
        val minLevel = min(clamped.first, clamped.last)
        val maxLevel = max(clamped.first, clamped.last)
        pokemon.level = if (minLevel == maxLevel) minLevel else Random.nextInt(minLevel, maxLevel + 1)
    }

    override fun apply(pokemonEntity: PokemonEntity) = apply(pokemonEntity.pokemon)

    override fun matches(pokemon: Pokemon) = pokemon.level in range
}

object LevelRangePropertyType : CustomPokemonPropertyType<LevelRangeProperty> {
    override val keys = setOf("levelRange")
    override val needsKey = true

    override fun fromString(value: String?): LevelRangeProperty? {
        val raw = value?.trim()?.ifEmpty { null } ?: return null
        val parts = raw.split("-", limit = 2)
        val (a, b) = when (parts.size) {
            1 -> {
                val v = parts[0].toIntOrNull() ?: return null
                v to v
            }
            2 -> {
                val v1 = parts[0].toIntOrNull() ?: return null
                val v2 = parts[1].toIntOrNull() ?: return null
                min(v1, v2) to max(v1, v2)
            }
            else -> return null
        }
        return LevelRangeProperty(a..b)
    }

    override fun examples() = listOf("levelRange=3-10", "levelRange=10-4", "levelRange=7")
}
