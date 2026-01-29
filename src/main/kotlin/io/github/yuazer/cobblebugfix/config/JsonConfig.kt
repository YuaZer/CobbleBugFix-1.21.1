package io.github.yuazer.cobblebugfix.config

import com.google.gson.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class JsonConfig internal constructor(
    private val root: JsonObject,
    val path: Path,
    private val gson: Gson
) {
    fun getString(key: String, default: String? = null): String? {
        val element = findElement(key)
        return when (element) {
            is JsonPrimitive if element.isString -> element.asString
            is JsonPrimitive -> element.asString
            is JsonObject, is JsonArray -> element.toString()
            else -> default
        }
    }

    fun getInt(key: String, default: Int = 0): Int {
        val element = findElement(key)
        return when (element) {
            is JsonPrimitive -> element.asNumber.toInt()
            else -> default
        }
    }

    fun getLong(key: String, default: Long = 0L): Long {
        val element = findElement(key)
        return when (element) {
            is JsonPrimitive -> element.asNumber.toLong()
            else -> default
        }
    }

    fun getDouble(key: String, default: Double = 0.0): Double {
        val element = findElement(key)
        return when (element) {
            is JsonPrimitive -> element.asNumber.toDouble()
            else -> default
        }
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        val element = findElement(key)
        return when (element) {
            is JsonPrimitive -> element.asNumber.toFloat()
            else -> default
        }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        val element = findElement(key)
        return when (element) {
            is JsonPrimitive if element.isBoolean -> element.asBoolean
            is JsonPrimitive -> element.asBoolean
            else -> default
        }
    }

    fun getStringList(key: String): List<String> {
        val element = findElement(key)
        return when (element) {
            is JsonArray -> element.mapNotNull { primitive ->
                when {
                    primitive.isJsonNull -> null
                    primitive.isJsonPrimitive -> primitive.asString
                    else -> primitive.toString()
                }
            }
            is JsonPrimitive -> listOf(element.asString)
            else -> emptyList()
        }
    }

    fun getObject(key: String): JsonObject? {
        val element = findElement(key)
        return element as? JsonObject
    }

    fun contains(key: String): Boolean = findElement(key) != null

    fun save(): JsonConfig {
        Files.createDirectories(path.parent)
        Files.newBufferedWriter(path).use { writer ->
            gson.toJson(root, writer)
        }
        return this
    }

    override fun toString(): String = gson.toJson(root)

    private fun findElement(key: String): JsonElement? {
        if (key.isBlank()) {
            return root
        }
        val segments = key.split('.').filter { it.isNotBlank() }
        if (segments.isEmpty()) {
            return root
        }
        var current: JsonElement = root
        for (segment in segments) {
            if (current is JsonObject) {
                current = current.get(segment) ?: return null
            } else {
                return null
            }
        }
        if (current is JsonNull) {
            return null
        }
        return current
    }

    companion object {
        internal val DEFAULT_GSON: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

        internal fun load(path: Path, gson: Gson = DEFAULT_GSON): JsonConfig {
            val root = readObject(path)
            return JsonConfig(root, path, gson)
        }

        private fun readObject(path: Path): JsonObject {
            if (Files.notExists(path)) {
                return JsonObject()
            }
            return try {
                Files.newBufferedReader(path).use { reader ->
                    val parsed = JsonParser.parseReader(reader)
                    (parsed as? JsonObject) ?: JsonObject()
                }
            } catch (_: IOException) {
                JsonObject()
            }
        }
    }
}
