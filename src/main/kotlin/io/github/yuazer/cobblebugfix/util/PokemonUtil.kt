package io.github.yuazer.cobblebugfix.util

import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.server
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.TagParser
import java.io.*
import java.util.Base64
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object PokemonUtil {
    fun getRegistryAccess() = server()!!.registryAccess()
    fun savePokemonToNBT(pokemon: Pokemon): CompoundTag {
        val nbt = CompoundTag()
        return pokemon.saveToNBT(server()!!.registryAccess(), nbt)
    }
    fun loadPokemonFromNBT(nbt: CompoundTag): Pokemon {
        val pokemon = Pokemon.loadFromNBT(server()!!.registryAccess(), nbt)
        return pokemon
    }

    /**
     * 推荐：Binary NBT -> Base64（可选GZIP）
     * - 体积小
     * - 解析快
     * - 不受 SNBT 的转义/格式影响
     */
    @JvmOverloads
    fun nbtToString(nbt: CompoundTag, gzip: Boolean = false): String {
        val baos = ByteArrayOutputStream()
        val out: OutputStream = if (gzip) GZIPOutputStream(baos) else baos

        out.use { os ->
            DataOutputStream(os).use { dos ->
                // 写入 NBT（包含 root name，读写配套即可）
                NbtIo.write(nbt, dos)
            }
        }

        return Base64.getEncoder().encodeToString(baos.toByteArray())
    }

    /**
     * 推荐：Base64 -> Binary NBT（可选GZIP）
     */
    @JvmOverloads
    fun stringToNbt(string: String, gzip: Boolean = false): CompoundTag {
        val bytes = Base64.getDecoder().decode(string)
        val bais = ByteArrayInputStream(bytes)
        val input: InputStream = if (gzip) GZIPInputStream(bais) else bais

        input.use { ins ->
            DataInputStream(ins).use { dis ->
                // 读回 NBT（要和 write 配套）
                val tag = NbtIo.read(dis, NbtAccounter.unlimitedHeap())
                return tag ?: CompoundTag()
            }
        }
    }

    /**
     * 备选：SNBT（可读字符串）——适合调试，不建议长期存库
     */
    fun nbtToSnbt(nbt: CompoundTag): String = nbt.toString()

    /**
     * 备选：SNBT -> NBT
     * - 解析失败会抛异常，你也可以包一层 runCatching
     */
    fun snbtToNbt(snbt: String): CompoundTag {
        val tag = TagParser.parseTag(snbt)
        return tag ?: CompoundTag()
    }
}
