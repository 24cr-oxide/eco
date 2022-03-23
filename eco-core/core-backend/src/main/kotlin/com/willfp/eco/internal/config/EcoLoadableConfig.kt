package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.LoadableConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Suppress("UNCHECKED_CAST")
open class EcoLoadableConfig(
    type: ConfigType,
    configName: String,
    private val plugin: PluginLike,
    private val subDirectoryPath: String,
    val source: Class<*>
) : EcoConfig(type), LoadableConfig {
    private val configFile: File
    private val name: String = "$configName.${type.extension}"
    private var hasChanged = false
    private val header = mutableListOf<String>()

    fun reloadFromFile() {
        runCatching { init(configFile) }.onFailure { it.printStackTrace() }
    }

    final override fun createFile() {
        val inputStream = source.getResourceAsStream(resourcePath)!!
        val outFile = File(this.plugin.dataFolder, resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(this.plugin.dataFolder, resourcePath.substring(0, lastIndex.coerceAtLeast(0)))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        if (!outFile.exists()) {
            val out: OutputStream = FileOutputStream(outFile)
            inputStream.copyTo(out)
            out.close()
            inputStream.close()
        }
    }

    override fun getResourcePath(): String {
        val resourcePath: String = if (subDirectoryPath.isEmpty()) {
            name
        } else {
            subDirectoryPath + name
        }
        return "/$resourcePath"
    }

    @Throws(IOException::class)
    override fun save() {
        if (!hasChanged) { // In order to preserve comments
            //return
        }

        val contents = StringBuilder()

        if (this.type == ConfigType.YAML) {
            for (s in header) {
                contents.append(s)
            }

            if (header.isNotEmpty()) {
                contents.append("\n")
            }
        }

        contents.append(this.toPlaintext())

        if (configFile.delete()) {
            Files.write(
                configFile.toPath(),
                contents.toString().toByteArray(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
            )
        }
    }

    private fun makeHeader(contents: String) {
        val lines = contents.split("\r\n").toList()

        if (this.type == ConfigType.YAML) {
            for (line in lines) {
                if (!line.startsWith("#")) {
                    break
                }

                header.add(line)
            }
        }
    }

    protected fun init(reader: Reader) {
        val string = reader.readToString()
        makeHeader(string)
        super.init(type.toMap(string))
    }

    fun init(file: File) {
        init(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
    }

    override fun getName(): String {
        return name
    }

    override fun getConfigFile(): File {
        return configFile
    }

    override fun set(path: String, obj: Any?) {
        hasChanged = true
        super.set(path, obj)
    }

    init {
        val directory = File(this.plugin.dataFolder, subDirectoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        if (!File(directory, name).exists()) {
            createFile()
        }
        configFile = File(directory, name)
        init(configFile)
        plugin.configHandler.addConfig(this)
    }
}