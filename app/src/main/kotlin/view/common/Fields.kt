/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import view.styles.AppColors
import view.styles.TextStyles
import java.awt.FileDialog

private val fieldHeight = 26.dp
private val fieldTitleMaxWidth = 160.dp
private val fieldBoxMaxWidth = 270.dp
private val fieldBoxContentPadding = PaddingValues(start = 7.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
private const val textInputFieldMaxChars = 50

@Composable
private fun field(title: String, fieldTitleWidth: Dp?, content: @Composable () -> Unit) {
    MaterialTheme(colors = AppColors.FieldColors) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                (if (fieldTitleWidth == null) Modifier else Modifier.width(fieldTitleWidth)).height(fieldHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bulletPoint()
                Text(title, style = TextStyles.mediumStyle)
            }
            if (fieldTitleWidth == fieldTitleMaxWidth || fieldTitleWidth == null) Spacer(Modifier.width(20.dp))
            content()
        }
    }
}

@Composable
private fun bulletPoint() {
    MaterialTheme(colors = AppColors.MainColors) {
        // Needed in order to use Canvas
        val bigCircleColor = MaterialTheme.colors.primary
        val smallCircleColor = MaterialTheme.colors.primaryVariant
        Box(modifier = Modifier.padding(end = 6.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(8.dp), onDraw = {
                drawCircle(color = bigCircleColor)
            })
            Canvas(modifier = Modifier.size(5.dp), onDraw = {
                drawCircle(color = smallCircleColor)
            })
        }
    }
}

@Composable
fun dropDownField(
    title: String,
    defaultOption: String,
    options: List<String>,
    hint: String,
    fieldTitleWidth: Dp? = fieldTitleMaxWidth,
    enabled: Boolean = true,
    onChange: (idx: Int, value: String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(defaultOption) }
    var expanded by remember { mutableStateOf(false) }

    if (options.isEmpty()) selectedOption = ""
    val fieldLocked = options.isEmpty() || !enabled

    field(title, fieldTitleWidth) {
        Column {
            TextButton(
                modifier = Modifier.height(fieldHeight).width(fieldBoxMaxWidth),
                enabled = !fieldLocked,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                onClick = { expanded = true },
                contentPadding = fieldBoxContentPadding,
                border = if (fieldLocked) BorderStroke(1.dp, MaterialTheme.colors.onBackground)
                else BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(MaterialTheme.colors.primaryVariant, MaterialTheme.colors.primary)),
                )
            ) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        text = selectedOption.ifEmpty { hint },
                        style = TextStyles.smallStyle,
                        color = if (fieldLocked || selectedOption.isEmpty()) MaterialTheme.colors.onBackground else MaterialTheme.colors.onPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Column(Modifier.width(27.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.requiredSize(20.dp),
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "contentDescription",
                            tint = if (fieldLocked) MaterialTheme.colors.onBackground else MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
            DropdownMenu(expanded, modifier = Modifier, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { idx, value ->
                    DropdownMenuItem(modifier = Modifier.height(fieldHeight).width(fieldBoxMaxWidth),
                        contentPadding = fieldBoxContentPadding,
                        onClick = {
                            if (selectedOption != value) {
                                onChange(idx, value)
                                selectedOption = value
                            }
                            expanded = false
                        }) {
                        Text(
                            text = value, style = TextStyles.smallStyle, overflow = TextOverflow.Ellipsis, maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun textInputField(
    title: String,
    fieldValue: String,
    hint: String,
    fieldTitleWidth: Dp? = fieldTitleMaxWidth,
    enabled: Boolean = true,
    onChange: (value: String) -> Unit
) {
    field(title, fieldTitleWidth) {
        Column {
            BasicTextField(value = fieldValue,
                onValueChange = { if (it.length <= textInputFieldMaxChars) onChange(it) },
                modifier = Modifier.width(fieldBoxMaxWidth).height(fieldHeight + 3.dp).background(
                    if (enabled) MaterialTheme.colors.background
                    else MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                ).padding(fieldBoxContentPadding),
                textStyle = TextStyles.smallStyle,
                enabled = enabled,
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        if (fieldValue.isBlank()) {
                            Text(
                                text = hint, color = MaterialTheme.colors.onBackground, style = TextStyles.smallStyle
                            )
                        }
                        innerTextField()
                    }
                })
            Divider(
                modifier = Modifier.width(fieldBoxMaxWidth),
                color = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
                thickness = 1.5f.dp
            )
        }
    }
}

@Composable
fun filePickerField(
    title: String,
    defaultFileName: String,
    extension: String,
    fieldTitleWidth: Dp? = fieldTitleMaxWidth,
    enabled: Boolean = true,
    onClickEdit: (() -> Unit)? = null,
    onFileChosen: (directory: String, file: String) -> Unit
) {
    var fileName by remember { mutableStateOf(defaultFileName) }
    var wrongFile by remember { mutableStateOf(false) }
    val fileIsSelected = fileName.isNotEmpty()

    field(title, fieldTitleWidth) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onClickEdit != null) {
                textButton("Edit", enabled = enabled) { onClickEdit() }
                Spacer(Modifier.width(10.dp))
            }
            rectangleButton("Choose", enabled = enabled) {
                val dialog = FileDialog(ComposeWindow(), "Choose a file", FileDialog.LOAD)
                dialog.isVisible = true
                val selectedFile = dialog.file
                if (selectedFile != null) {
                    if (selectedFile.endsWith(extension)) {
                        onFileChosen(dialog.directory, selectedFile)
                        fileName = selectedFile
                    } else {
                        onFileChosen("", "")
                        fileName = ""
                        wrongFile = true
                    }
                }
            }
            Text(
                modifier = (if (fileIsSelected) Modifier.width(fieldTitleMaxWidth) else Modifier).padding(start = 10.dp),
                text = if (fileIsSelected) fileName else if (wrongFile) "Please choose a $extension file" else "No file chosen",
                style = TextStyles.smallStyle,
                color = if (fileIsSelected) Color.Black else if (wrongFile) MaterialTheme.colors.onError else Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}
