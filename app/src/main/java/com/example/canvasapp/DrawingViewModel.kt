package com.example.canvasapp

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList()
)

val allColors = listOf(
    Color.Black,
    Color.Blue,
    Color.Red,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan,
    Color.Green,
)

data class PathData(
    val id: String,
    val color: Color,
    val path: List<Offset>
)

sealed interface DrawingAction {
    data object onNewPathStart: DrawingAction
    data class onDraw(val offset: Offset): DrawingAction
    data object onPathEnd: DrawingAction
    data class onSelectColor(val color: Color): DrawingAction
    data object onClearCanvas: DrawingAction
}

class DrawingViewModel: ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onAction(action: DrawingAction) {
        when(action) {
            DrawingAction.onClearCanvas -> onClearCanvas()
            is DrawingAction.onDraw -> onDraw(action.offset)
            DrawingAction.onNewPathStart -> onNewPathStart()
            DrawingAction.onPathEnd -> onPathEnd()
            is DrawingAction.onSelectColor -> onSelectColor(action.color)
        }
    }

    private fun onNewPathStart() {
        _state.update { it.copy(
            currentPath = PathData(
                id = System.currentTimeMillis().toString(),
                color = it.selectedColor,
                path = emptyList()
            )
        ) }
    }

    private fun onClearCanvas() {
        _state.update { it.copy(
            currentPath = null,
            paths = emptyList()
        ) }
    }

    private fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return
        _state.update { it.copy(
            currentPath = currentPathData.copy(
                path = currentPathData.path + offset
            )
        ) }
    }

    private fun onPathEnd() {
        val currentPathData = state.value.currentPath ?: return
        _state.update { it.copy(
            currentPath = null,
            paths = it.paths + currentPathData
        ) }
    }

    private fun onSelectColor(color: Color) {
        _state.update { it.copy(
            selectedColor = color
        ) }
    }

}