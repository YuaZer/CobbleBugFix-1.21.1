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
    override fun asString() = "levelrange=${range.first}-${range.last}"

    private fun clampedRange(): IntRange {
        val a = range.first.coerceIn(1, Cobblemon.config.maxPokemonLevel)
        val b = range.last.coerceIn(1, Cobblemon.config.maxPokemonLevel)
        val minLevel = min(a, b)
        val maxLevel = max(a, b)
        return minLevel..maxLevel
    }

    override fun apply(pokemon: Pokemon) {
        val clamped = clampedRange()
        pokemon.level = if (clamped.first == clamped.last) clamped.first else Random.nextInt(clamped.first, clamped.last + 1)
    }

    override fun apply(pokemonEntity: PokemonEntity) = apply(pokemonEntity.pokemon)

    override fun matches(pokemon: Pokemon) = pokemon.level in clampedRange()
}

object LevelRangePropertyType : CustomPokemonPropertyType<LevelRangeProperty> {
    override val keys = setOf("levelrange")
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

    override fun examples() = listOf("3-10", "10-4", "7")
}
