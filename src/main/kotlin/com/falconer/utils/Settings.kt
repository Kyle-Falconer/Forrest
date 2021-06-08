package com.falconer.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp

object Settings {
    var fontSize by mutableStateOf(13.sp)

    val saveFileName = "forrest_config.json"
    var saveFilePath by mutableStateOf(System.getProperty("user.home"))
}