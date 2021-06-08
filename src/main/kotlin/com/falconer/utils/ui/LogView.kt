package com.falconer.utils.ui

//
//@Composable
//fun LogView(model: Runner, settings: Settings) {
//    with (LocalDensity.current) {
//        SelectionContainer {
//            Surface(
//                Modifier.fillMaxSize(),
//                color = AppTheme.colors.background,
//            ) {
//                val lines: MutableState<String> by remember(mutableListOf(model.logTail))
//
//                if (lines != null) {
//                    Box {
//                        Lines(lines!!, settings)
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//private fun Lines(lines: MutableList<String>, settings: Settings) = with(LocalDensity.current) {
//    val maxNum = remember(lines.lineNumberDigitCount) {
//        (1..lines.lineNumberDigitCount).joinToString(separator = "") { "9" }
//    }
//
//    Box(Modifier.fillMaxSize()) {
//        val scrollState = rememberScrollState(0)
//        val lineHeight = settings.fontSize.toDp() * 1.6f
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            state = scrollState
//        ) {
//            items(lines) { index ->
//                TextBox(lines[index])
//                Box(Modifier.height(lineHeight)) {
//                    MouseScrollUnit.Line(Modifier.align(Alignment.CenterStart), maxNum, lines[index], settings)
//                }
//            }
//        }
//
//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
//            adapter= rememberScrollbarAdapter(scrollState)
//        )
//    }
//}