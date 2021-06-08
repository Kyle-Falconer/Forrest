package com.falconer.utils

import androidx.compose.desktop.Window
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.falconer.utils.data.FileReadWriter
import com.falconer.utils.task.Target.Companion.LOCAL_HOST
import com.falconer.utils.task.Controller
import com.falconer.utils.task.PortRunner
import com.falconer.utils.task.Target
import com.falconer.utils.task.TargetConfig
import com.falconer.utils.ui.taskView

object MainWindowValues {
    val windowPos = mutableStateOf(IntOffset.Zero)
    val windowSize = mutableStateOf(IntSize.Zero)
}

fun main() = Window (events = WindowEvents(
    onRelocate = { location ->
        println("main window is moving to $location")
        MainWindowValues.windowPos.value = location
    },
    onResize = { windowSize ->
        println("main window size is now $windowSize")
        MainWindowValues.windowSize.value = windowSize
    }
)){
    val controller = Controller()

    fun preloadTasks() {

        val savedRunners = FileReadWriter.loadRunnersFromFile(Settings.saveFilePath)
        if (savedRunners.isNotEmpty()) {
            for (runner in savedRunners) {
                if (!controller.targets.contains(runner.target)) {
                    controller.targets.add(runner.target)
                }
                controller.addRunner(runner)
            }
        } else {
            val remoteTarget = Target(
                "cloud desktop",
                TargetConfig(
                    "someremotecomputer.fqdn.com",
                    "\$HOME/.ssh/id_rsa"
                )
            )
            controller.addTarget(LOCAL_HOST)
            controller.addTarget(remoteTarget)
            controller.addRunner(PortRunner("HTTP 8080 Tunnel", localPort = 8080, remotePort = 8080))

            FileReadWriter.saveRunnersToFile(Settings.saveFilePath, controller.runners)
        }
    }

    MaterialTheme {

        preloadTasks()
        Column {
            for (runner in controller.runners) {
                taskView(runner, controller)
                Divider(color = Color.Black)
            }
        }
    }

}