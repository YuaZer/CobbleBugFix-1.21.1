package io.github.yuazer.cobblebugfix.config

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

object JsonConfigManager {
    private const val RESOURCE_PREFIX = "config/"
    private val gson: Gson = JsonConfig.DEFAULT_GSON

    fun register(holder: Any) {
        val kClass = holder::class
        for (property in kClass.memberProperties) {
            val annotation = property.findAnnotation<Config>()
                ?: property.javaField?.getAnnotation(Config::class.java)
                ?: continue
            if (property !is KMutableProperty1<*, *>) {
                continue
            }
            val returnType = property.returnType.jvmErasure
            if (!JsonConfig::class.java.isAssignableFrom(returnType.java)) {
                continue
            }
            @Suppress("UNCHECKED_CAST")
            val typedProperty = property as KMutableProperty1<Any, JsonConfig>
            val loaded = loadConfig(annotation.path)
            val field = typedProperty.javaField
            if (field != null) {
                field.isAccessible = true
                field.set(holder, loaded)
                continue
            }
            // Fallback: try setter if no backing field is available.
            val setter = typedProperty.setter
            if (setter.parameters.size == 1) {
                setter.call(loaded)
            } else {
                setter.call(holder, loaded)
            }
        }
    }

    private fun loadConfig(relativePath: String): JsonConfig {
        val friendlyPath = sanitizePath(relativePath)
        if (friendlyPath.isBlank()) {
            throw IllegalArgumentException("Config path cannot be empty")
        }
        val configDir = FabricLoader.getInstance().configDir
        val destination = configDir.resolve(friendlyPath)
        Files.createDirectories(destination.parent ?: configDir)
        if (Files.notExists(destination)) {
            releaseDefault(friendlyPath, destination)
        }
        return JsonConfig.load(destination, gson)
    }

    private fun releaseDefault(relativePath: String, destination: Path) {
        val resourcePath = RESOURCE_PREFIX + relativePath
        val resourceStream = JsonConfigManager::class.java.classLoader.getResourceAsStream(resourcePath)
        if (resourceStream != null) {
            resourceStream.use { Files.copy(it, destination) }
            return
        }
        try {
            Files.newBufferedWriter(destination).use { writer ->
                writer.write("{}")
            }
        } catch (ignored: IOException) {
        }
    }

    private fun sanitizePath(input: String): String {
        return input.replace('\\', '/').trimStart('/').trim()
    }
}
