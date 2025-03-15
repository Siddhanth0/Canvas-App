package com.example.canvasapp

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DrawingState(
    val selectedColor: Color = Color.Black,
    val selectedBrushSize: Float = 10f, // Added brush size
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
    val brushSize: Float,
    val path: List<Offset>
)

sealed interface DrawingAction {
    data class OnNewPathStart(val color: Color, val brushSize: Float) : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction
    data class OnSelectColor(val color: Color) : DrawingAction
    data object OnClearCanvas : DrawingAction
    data class OnBrushSizeChange(val size: Float) : DrawingAction
}

class DrawingViewModel : ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvas -> onClearCanvas()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            is DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnBrushSizeChange -> onBrushSizeChange(action.size)
        }
    }

    private fun onNewPathStart() {
        _state.update { state ->
            state.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = state.selectedColor,
                    brushSize = state.selectedBrushSize,
                    path = emptyList()
                )
            )
        }
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

    private fun onBrushSizeChange(size: Float) {
        _state.update { it.copy(
            selectedBrushSize = size
        ) }
    }
}
