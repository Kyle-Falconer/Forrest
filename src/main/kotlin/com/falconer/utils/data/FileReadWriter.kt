package com.falconer.utils.data

import com.falconer.utils.Settings
import com.falconer.utils.task.Runner
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.IOException

object FileReadWriter {
    fun saveRunnersToFile(fileName: String, model: MutableList<Runner>) {
        val saveFile = File(fileName+File.separator+ Settings.saveFileName)
        val modelJson = serializeRunners(model)
        writeJsonToFile(saveFile, modelJson)
    }

    fun loadRunnersFromFile(fileName: String): List<Runner> {
        val saveFile = File(fileName+File.separator+ Settings.saveFileName)
        try {
            val modelJson = readJsonFromFile(saveFile)
            return deserializeRunners(modelJson)
        } catch (e :IOException) {
            e.printStackTrace()
        }
        return listOf()
    }

    private fun serializeRunners(model: MutableList<Runner>): String {
        val mapper = jacksonObjectMapper()
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model)
    }

    private fun deserializeRunners(jsonTasks: String): List<Runner> {
//        println("interpreting JSON as Runner objects:\n$jsonTasks")
        val mapper = jacksonObjectMapper()
        return mapper.readValue(jsonTasks)
    }

    private fun writeJsonToFile(file: File, json: String) {
        if (!file.exists()) {
            file.createNewFile()
        }
        println("writing JSON to file: $file")
        file.bufferedWriter().use { out -> out.write(json) }
    }

    private fun readJsonFromFile(file: File): String {
        if (!file.exists()) {
            throw IOException("file does not exist: ${file.absolutePath}")
        }
        println("reading JSON from file: $file")
        return file.inputStream().readBytes().toString(Charsets.UTF_8)
    }
}