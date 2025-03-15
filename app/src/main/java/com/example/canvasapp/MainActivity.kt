package com.example.canvasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canvasapp.ui.theme.CanvasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CanvasAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<DrawingViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DrawingCanvas(
                            paths = state.paths,
                            currentPath = state.currentPath,
                            selectedColor = state.selectedColor,
                            brushSize = state.selectedBrushSize,
                            onAction = viewModel::onAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        CanvasControls(
                            selectedColor = state.selectedColor,
                            selectedBrushSize = state.selectedBrushSize,
                            colors = allColors,
                            onSelectColor = {
                                viewModel.onAction(DrawingAction.OnSelectColor(it))
                            },
                            onBrushSizeChange = { viewModel.onAction(DrawingAction.OnBrushSizeChange(it)) },
                            onClearCanvas = {
                                viewModel.onAction(DrawingAction.OnClearCanvas)
                            }
                        )
                    }
                }
            }
        }
    }
}