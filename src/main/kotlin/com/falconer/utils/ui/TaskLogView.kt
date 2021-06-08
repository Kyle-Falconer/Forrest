package com.falconer.utils.ui

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.falconer.utils.task.Runner
import com.falconer.utils.ui.AppTheme.colors

@Composable
fun taskLogView(model: Runner) {
    val logText = remember { mutableStateOf(model.logTail) }
    DesktopMaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Text(modifier = Modifier.padding(10.dp), color = colors.textPrimary, text = "Logs for \"${model.name}\"")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp, bottom = 12.dp)
                    .background(color = colors.textFieldBackground)
            ) {
                SelectionContainer {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(logText.value) { item ->
                            Text(color = colors.textFieldForeground, text = item)
                        }
                    }
                }
            }

        }
    }
}