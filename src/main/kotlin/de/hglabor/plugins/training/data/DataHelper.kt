package de.hglabor.plugins.training.data

import de.hglabor.plugins.training.main.PLUGIN
import org.apache.logging.log4j.core.util.FileUtils
import org.bukkit.Bukkit
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

private val DATA: String = PLUGIN.dataFolder.absolutePath + File.separator + "data" + File.separator
fun plIn(fileName: String) = FileInputStream("$DATA$fileName.data")
fun plOut(fileName: String) = FileOutputStream("$DATA$fileName.data")

/** Get a serializable object from the given file path */
fun loadObj(fileName: String): Serializable? =
    try {
        val fileIn = plIn(fileName)
        val gzipIn = GZIPInputStream(fileIn)
        val bukkitIn = BukkitObjectInputStream(gzipIn)
        bukkitIn.readObject() as Serializable
    }
    catch (e: FileNotFoundException) {
        Bukkit.getLogger().warning("Data file for $fileName.data not yet created.")
        null
    }
    catch (e: IOException) {
        e.printStackTrace()
        null
    }

/** Save a serializable object to the given file path */
fun saveObj(fileName: String, obj: Serializable) =
    try {
        FileUtils.mkdir(File(DATA), true)
        val fileOut = plOut(fileName)
        val gzipOut = GZIPOutputStream(fileOut)
        val bukkitOut = BukkitObjectOutputStream(gzipOut)
        bukkitOut.writeObject(obj)
        bukkitOut.close()
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }