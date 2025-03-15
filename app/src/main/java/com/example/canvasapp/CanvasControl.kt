package com.example.canvasapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasControls(
    selectedColor: Color,
    selectedBrushSize: Float,
    colors: List<Color>,
    onSelectColor: (Color) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onClearCanvas: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val sliderColors = SliderColors(
        thumbColor = Color.White,
        activeTrackColor = Color.White,
        activeTickColor = Color.Transparent,
        inactiveTrackColor = Color.Black,
        inactiveTickColor = Color.Transparent,
        disabledThumbColor = Color.Black,
        disabledActiveTrackColor = Color.Black,
        disabledActiveTickColor = Color.Transparent,
        disabledInactiveTrackColor = Color.Black,
        disabledInactiveTickColor = Color.Transparent
    )


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { showColorPicker = true }
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.color_picker),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            items(colors) { color ->
                val isSelected = selectedColor == color
                val scale = if (isSelected) 1.2f else 1f
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onSelectColor(color) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Brush Size: ${selectedBrushSize.toInt()}")
        Slider(
            value = selectedBrushSize,
            onValueChange = onBrushSizeChange,
            valueRange = 5f..50f,
            colors = sliderColors,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.size(20.dp), // Customize thumb size
                    colors = sliderColors
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(colors = ButtonColors(Color.White, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.White),
            onClick = onClearCanvas) {
                Text("Clear Canvas")
            }
    }

    if (showColorPicker) {
        CustomColorPickerDialog(
            onDismissRequest = { showColorPicker = false },
            onPickedColor = { color ->
                val pickedColor = Color(color.red, color.green, color.blue, color.alpha)
                onSelectColor(pickedColor)
                showColorPicker = false
            }
        )
    }
}

@Composable
fun CustomColorPickerDialog(
    onDismissRequest: () -> Unit,
    onPickedColor: (Color) -> Unit
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }

    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Color.Black,
        confirmButton = {
            Button(colors = ButtonColors(Color.White, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.White),
                onClick = {
                    onPickedColor(Color.hsv(hue, saturation, 1f))
                    onDismissRequest()
                }
            ) {
                Text("Select Color")
            }
        },
        dismissButton = {
            Button(colors = ButtonColors(Color.White, contentColor = Color.Black, disabledContainerColor = Color.DarkGray, disabledContentColor = Color.White),
                onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = { Text(text = "Pick a Color", color = Color.White) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                touchPosition = Offset(
                                    x = offset.x.coerceIn(0f, size.width.toFloat()),
                                    y = offset.y.coerceIn(0f, size.height.toFloat())
                                )
                                hue = ((touchPosition.x / size.width) * 360f).coerceIn(0f, 360f)
                                saturation = (1f - (touchPosition.y / size.height)).coerceIn(0f, 1f)
                            },
                            onDrag = { change, _ ->
                                touchPosition = Offset(
                                    x = change.position.x.coerceIn(0f, size.width.toFloat()),
                                    y = change.position.y.coerceIn(0f, size.height.toFloat())
                                )
                                hue = ((touchPosition.x / size.width) * 360f).coerceIn(0f, 360f)
                                saturation = (1f - (touchPosition.y / size.height)).coerceIn(0f, 1f)
                            }
                        )
                    }
                    .drawBehind {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = (0..360 step 10).map {
                                    Color.hsv(it.toFloat(), 1f, 1f)
                                }
                            )
                        )
                        drawRect(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.White)
                            )
                        )

                        drawCircle(
                            color = Color.Black,
                            radius = 10f,
                            center = touchPosition
                        )
                    }
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}