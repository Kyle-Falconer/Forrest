package com.falconer.utils.ui

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falconer.utils.MainWindowValues
import com.falconer.utils.task.*
import java.awt.image.BufferedImage

@Composable
fun jobTitle(name: String) {
    Text(
        text = name,
        textAlign = TextAlign.Start,
        style = typography.h6.copy(fontSize = 14.sp),
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun statusFail() {
    Box(
        modifier = Modifier
            .defaultMinSize(80.dp)
            .background(Color.White),
    ) {
        Text(
            "X",
            Modifier.padding(12.dp),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(239, 31, 34)
        )
    }
}

@Composable
fun statusOK() {
    Box(
        modifier = Modifier
            .defaultMinSize(80.dp)
            .background(Color.White)
    ) {
        Text(
            "OK",
            Modifier.padding(12.dp),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(119, 239, 33),
        )
    }
}


@Composable
fun statusBlock(runningState: MutableState<Boolean>) {
    if (runningState.value) {
        statusOK()
    } else {
        statusFail()
    }
}

@Composable
fun startStopButtons(runner: Runner) {
    Button(modifier = Modifier.padding(6.dp),
        onClick = {
            println("start button clicked for runner: ${runner.name}")
            runner.start()
        }) {
        Text("start")
    }
    Button(modifier = Modifier.padding(6.dp),
        onClick = {
            println("stop button clicked for runner: ${runner.name}")
            runner.stop()
        }) {
        Text("stop")
    }
}

@Composable
fun genericJobRow(runner: Runner, icon: BufferedImage?, controller: Controller) {
    val jobState = remember { runner.isRunning }
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.CenterStart),
        verticalAlignment = Alignment.CenterVertically
    ) {
        statusBlock(jobState)
        jobTitle(runner.name)

        Row(
            modifier = Modifier.fillMaxWidth().fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            startStopButtons(runner)
            editButton(model = runner, controller = controller)
            logsButton(model=runner)
        }
    }
}

@Composable
fun longJobView(runner: Runner, controller: Controller) {
    genericJobRow(runner, null, controller = controller)
}

@Composable
fun editButton(model: Runner, controller: Controller) {
    Button(modifier = Modifier.padding(6.dp),
        onClick = {
            println("edit button clicked for runner: ${model.name}")
            AppWindow(size = IntSize(400, 600)).also {
                it.keyboard.setShortcut(Key.Escape) {
                    it.close()
                }
            }.show {
                editTaskView(model = model, controller = controller)
            }
        }) {
        Text("edit")
    }
}

@Composable
fun logsButton(model: Runner) {
    val mainWindowPos = remember{ mutableStateOf( MainWindowValues.windowPos) }
    val mainWindowSize = remember{ mutableStateOf( MainWindowValues.windowSize) }
    val yPos = mutableStateOf(LocalAppWindow.current.y)
    Button(modifier = Modifier.padding(6.dp),
        onClick = {
            println("edit button clicked for runner: ${model.name}")

            AppWindow(size = IntSize(800, 600)).also {  // location = IntOffset(xOffset*2, 1 )
                it.keyboard.setShortcut(Key.Escape) {
                    it.close()
                }
            } .show {
                taskLogView(model = model)
                LocalAppWindow.current.window.setLocation(mainWindowPos.value.value.x + mainWindowSize.value.value.width, mainWindowPos.value.value.y)
            }
        }) {
        Text("logs")
    }
}

@Composable
fun portForwardJobView(runner: Runner, controller: Controller) {
    val jobState = remember { runner.isRunning }
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.CenterStart),
        verticalAlignment = Alignment.CenterVertically
    ) {
        statusBlock(jobState)
        jobTitle(runner.name)

        Row(
            modifier = Modifier.fillMaxWidth().fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            startStopButtons(runner)
            Button(modifier = Modifier.padding(6.dp),
                onClick = {
                    println("kill port button clicked for runner: ${runner.name}")
                    val localPort = (runner as PortRunner).localPort
                    val pKiller = PortKiller("Port Kill $localPort", localPort)
                    pKiller.start()
                }) {
                Text("kill port ${(runner as PortRunner).localPort}")
            }
            editButton(model = runner, controller = controller)
            logsButton(model=runner)
        }
    }
}


@Composable
fun cookieJobView(runner: Runner, controller: Controller) {
    genericJobRow(runner, null, controller = controller)
}


@Composable
fun monitorJobView(runner: Runner, controller: Controller) {
    genericJobRow(runner, null, controller = controller)
}

@Composable
fun monitorDockerContainer(runner: Runner, controller: Controller) {
    genericJobRow(runner, null, controller = controller)
}

@Composable
fun taskView(runner: Runner, controller: Controller) {
    Row() {
        @Suppress("UNUSED_VARIABLE")
        val exhaustive = when (runner.runnerType) {
            RunnerType.LONG_JOB -> longJobView(runner, controller)
            RunnerType.PORT -> portForwardJobView(runner, controller)
            RunnerType.COOKIE -> cookieJobView(runner, controller)
            RunnerType.MONITOR -> monitorJobView(runner, controller)
            RunnerType.DOCKER_MONITOR -> monitorDockerContainer(runner, controller)
        }
    }
}