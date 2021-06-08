package com.falconer.utils.ui

import androidx.compose.desktop.AppManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.falconer.utils.task.*

@Composable
fun saveButton(originalModel: Runner, updatedModel: Runner, controller: Controller) {
    Button(modifier=Modifier.padding(10.dp), onClick = {
        controller.updateRunner(originalRunner=originalModel, updatedRunner=updatedModel)
        AppManager.focusedWindow?.close()
    }) {
        Text("Save changes")
    }
}

@Composable
fun cancelButton() {
    Button(modifier=Modifier.padding(10.dp), onClick = {
        AppManager.focusedWindow?.close()
    }) {
        Text("cancel")
    }
}

@Composable
fun editFieldsForPortType(model: PortRunner, controller: Controller) {
    var runnerName by remember { mutableStateOf(model.name) }
    var localPort by remember { mutableStateOf(model.localPort) }
    var remotePort by remember { mutableStateOf(model.remotePort) }
    Column (modifier = Modifier.fillMaxWidth().fillMaxWidth().fillMaxHeight().padding(10.dp)){
        OutlinedTextField( label= {Text("name")}, value = runnerName, onValueChange = { runnerName=it })
        OutlinedTextField( label= {Text("local port")}, value =localPort.toString(), onValueChange = { localPort=Integer.parseInt(it) })
        OutlinedTextField( label= {Text("remote port")}, value =remotePort.toString(), onValueChange = { remotePort=Integer.parseInt(it) })
        Row(modifier = Modifier.fillMaxWidth().fillMaxWidth().fillMaxHeight().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom) {
            cancelButton()
            saveButton(originalModel =model,
                updatedModel = PortRunner(name = runnerName, localPort=localPort, remotePort = remotePort),
                controller = controller)
        }
    }
}

@Composable
fun editFieldsForLongJob(model: LongRunner, controller: Controller) {
    var runnerName by remember { mutableStateOf(model.name) }
    var userScript by remember { mutableStateOf(model.userScript) }
    Column (modifier = Modifier.fillMaxWidth().fillMaxWidth().fillMaxHeight().padding(10.dp)){
        OutlinedTextField( label= {Text("name")}, value = runnerName, onValueChange = { runnerName=it })
        OutlinedTextField( label= {Text("script")}, value =userScript, onValueChange = { userScript=it })
        Row(modifier = Modifier.fillMaxWidth().fillMaxWidth().fillMaxHeight().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom) {
            cancelButton()
            saveButton(originalModel =model,
                updatedModel = LongRunner(name = runnerName, userScript=userScript),
                controller = controller)
        }
    }
}


@Composable
fun editFieldsForNOTYETIMPLEMENTED(model: Runner, controller: Controller) {
    Column (modifier = Modifier.fillMaxWidth().fillMaxWidth().fillMaxHeight().padding(10.dp)){
        Text("Editing of type ${model.runnerType} is not yet implemented")
        Row(modifier = Modifier.fillMaxWidth().fillMaxWidth().fillMaxHeight().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom) {
            cancelButton()
        }
    }
}

@Composable
fun editTaskView(controller: Controller, model: Runner) {
    val modelUnderEdit = model.clone()

    when(model.runnerType) {
        RunnerType.PORT -> editFieldsForPortType(modelUnderEdit as PortRunner, controller)
        RunnerType.LONG_JOB -> editFieldsForLongJob(modelUnderEdit as LongRunner, controller)
        RunnerType.COOKIE -> editFieldsForNOTYETIMPLEMENTED(modelUnderEdit, controller)
        RunnerType.MONITOR -> editFieldsForNOTYETIMPLEMENTED(modelUnderEdit, controller)
        RunnerType.DOCKER_MONITOR -> editFieldsForNOTYETIMPLEMENTED(modelUnderEdit, controller)
    }
}