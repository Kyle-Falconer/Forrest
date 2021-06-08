package com.falconer.utils.task

import androidx.compose.runtime.mutableStateOf
import com.falconer.utils.task.Target.Companion.LOCAL_HOST
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.*
import java.util.*
import kotlin.concurrent.thread


enum class RunnerType {
    PORT, LONG_JOB, COOKIE, MONITOR, DOCKER_MONITOR
}

class StreamMuxer(private val inputStream: InputStreamReader, val stringOutput: MutableList<String>) : Thread() {
    override fun run() {
        try {
            val bufReader = BufferedReader(inputStream)
            var line: String? = bufReader.readLine()
            while (line != null) {
                stringOutput.add(line)
                println(line)
                line = bufReader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "runnerType")
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = PortRunner::class, name = "PORT"),
        JsonSubTypes.Type(value = LongRunner::class, name = "LONG_JOB"),
        JsonSubTypes.Type(value = CookieRunner::class, name = "COOKIE"),
        JsonSubTypes.Type(value = Monitor::class, name = "MONITOR"),
        JsonSubTypes.Type(value = DockerMonitor::class, name = "DOCKER_MONITOR")
    ]
)
abstract class Runner(val name: String, val runnerType: RunnerType, val target: Target = LOCAL_HOST) {

    @Volatile
    var logTail = Collections.synchronizedList<String>(mutableListOf())   // FIXME: need to make this thread safe

    @Volatile
    var isRunning = mutableStateOf(false)

    @Volatile
    private var process: Process? = null

    private var processThread : Thread? = null

    @JsonIgnore
    abstract fun getScript(): String

    fun start() {
        logTail.clear()
        isRunning.value = true
        processThread = thread(start = true) {
            execute(getScript())
        }

    }

    fun stop() {
        println("sending stop signal for \"$name\"")
        processThread?.interrupt()
        isRunning.value = false
        process?.destroy()
    }

    /**
     * Produce a file containing the command to be run through bash. This is done to avoid potential errors with
     * more complex scripts.
     * @param command The command to write to a temporary file as a script
     * @return The path to the temporary script
     */
    private fun generateTempScript(command: String): File {

        val tmpScript = File.createTempFile("Forrest_tmp_${System.currentTimeMillis()}", null)
        val streamWriter: Writer = OutputStreamWriter(FileOutputStream(tmpScript))
        val printWriter = PrintWriter(streamWriter)

        printWriter.println("#!/bin/bash")
        if (target.targetConfig.hostname == LOCAL_HOST.targetConfig.hostname) {
            printWriter.println(command)
        } else {
            // uses a heredoc to execute the command over ssh
            printWriter.print("ssh -tt ")
            printWriter.print("-o \"IdentitiesOnly=yes\" -i ${target.targetConfig.sslCertPath} ")
            printWriter.print("${target.targetConfig.hostname} <<'EOL'\n")
            printWriter.println(command)
            printWriter.println("exit")
            printWriter.println("EOL")
        }
        printWriter.close()
        println("created temp script at ${tmpScript.absolutePath}")
        return tmpScript
    }

    /**
     * Deletes the temporary script file.
     * @param tmpScript The temporary file to delete.
     */
    private fun destroyTempScript(tmpScript: File) {
        println("deleting temp script at ${tmpScript.absolutePath}")
        tmpScript.delete()
    }

    protected fun execute(command: String, optCwd: Optional<File> = Optional.empty()) {
        val workingDir = if (optCwd.isPresent) optCwd.get() else File(System.getProperty("user.home"))
        val tmpScript = generateTempScript(command)

        println("Preparing this script to run on ${target.computerName}:\n\t$tmpScript")

        process = ProcessBuilder("bash", tmpScript.toString())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        process?.let { p ->
            val pid = p.pid()
            println("started process with PID=$pid")

            val stdinStream = InputStreamReader(p.inputStream)
            val stdoutStream = OutputStreamWriter(p.outputStream)
            val stderrStream = InputStreamReader(p.errorStream)

            val outputMuxer = StreamMuxer(stdinStream, logTail)
            outputMuxer.start()

            val errMuxer = StreamMuxer(stderrStream, logTail)
            errMuxer.start()
            println("waiting for the process (PID=${process?.pid()}) to finish")
            var killTime = -1L
            while (p.isAlive) { // listen for the stop flag
                try {
                    Thread.sleep(SLEEP_TIME_MS)
                } catch (e: InterruptedException) {
                    println("thread received wakeup call")
                }
                if (!isRunning.value) { // try to stop the process normally
                    if (killTime == -1L) {
                        killTime = System.currentTimeMillis()
                        println("destroying the process for $name, PID=$pid")
                        p.destroy()
                    } else if (System.currentTimeMillis() - killTime >= KILL_TIMEOUT_MS) { // been waiting too long, kill it!
                        println("forcibly destroying the process for $name, PID=$pid")
                        p.destroyForcibly()
                    }
                }
            }
            val retVal = p.waitFor()
            if (retVal == 0) {
                println("process completed OK")
            } else if (retVal == 143) {
                println("process stopped with SIGTERM")
            } else {
                println("process failed with exit code $retVal")
            }
        }

        isRunning.value = false
        destroyTempScript(tmpScript)
    }

    abstract fun clone(): Runner

    companion object {
        private const val KILL_TIMEOUT_MS = 1000 * 1L
        private const val SLEEP_TIME_MS = 1000 * 1L
    }
}


class PortRunner(name: String, val localPort: Int, val remotePort: Int, target: Target = LOCAL_HOST) : Runner(
    name = name,
    runnerType = RunnerType.PORT,
    target = target
) {
    override fun getScript(): String {
        return "ssh dev -N -L $localPort:localhost:$remotePort %h"
    }

    override fun clone(): Runner {
        return PortRunner(name = name, localPort = localPort, remotePort = remotePort, target = target)
    }
}

class PortKiller(name: String, val localPort: Int, target: Target = LOCAL_HOST) : Runner(
    name = name,
    RunnerType.PORT,
    target = target
) {
    override fun getScript(): String {
        return "pid=\$(lsof -i:$localPort -t); kill -15 \$pid  2> /dev/null || kill -9 \$pid 2> /dev/null"
    }

    override fun clone(): Runner {
        return PortKiller(name = name, localPort = localPort, target = target)
    }
}

class LongRunner(name: String, val userScript: String, target: Target = LOCAL_HOST) : Runner(
    name = name,
    RunnerType.LONG_JOB,
    target = target
) {
    override fun getScript(): String {
        return userScript
    }

    override fun clone(): Runner {
        return LongRunner(name = name, userScript = userScript, target = target)
    }
}

class CookieRunner(name: String, val userScript: String, target: Target = LOCAL_HOST) : Runner(
    name = name,
    RunnerType.COOKIE,
    target = target
) {
    override fun getScript(): String {
        return userScript
    }

    override fun clone(): Runner {
        return CookieRunner(name = name, userScript = userScript, target = target)
    }
}

class Monitor(name: String, val pid: Int, target: Target = LOCAL_HOST) : Runner(
    name = name,
    RunnerType.MONITOR,
    target = target
) {
    override fun getScript(): String {
        return "kill -0 $pid"   // TODO: write a script to grab the status of the docker container
    }

    override fun clone(): Runner {
        return Monitor(name = name, pid = pid, target = target)
    }
}

class DockerMonitor(name: String, val containerName: String, target: Target = LOCAL_HOST) : Runner(
    name = name,
    RunnerType.DOCKER_MONITOR,
    target = target
) {
    override fun getScript(): String {
        // script to get only names: "docker ps --format '{{.Names}}'"
        return containerName    // TODO: write a script to grab the status of the docker container
    }

    override fun clone(): Runner {
        return DockerMonitor(name = name, containerName = containerName, target = target)
    }
}