/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.development

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import view.common.closeButton
import view.common.dialog
import view.common.filledButton
import view.common.outlinedButton
import view.styles.Modifiers
import view.styles.TextStyles

@Composable
fun codeWindow(
    code: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
    disabledMsg: String = if (readOnly) "Code viewer disabled" else "Code editor disabled",
    onChange: (value: String) -> Unit = {}
) {
    val codeModifier = modifier.background(if (enabled) MaterialTheme.colors.secondary else Color.Unspecified)
        .border(BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)).padding(10.dp)
    Box {
        if (!enabled) Text(
            disabledMsg,
            modifier = Modifier.background(Color.LightGray).padding(5.dp),
            style = TextStyles.codeStyle,
            color = Color.DarkGray
        )
        if (readOnly) {
            BasicTextField(value = code,
                modifier = codeModifier,
                textStyle = TextStyles.codeStyle,
                maxLines = 2000,
                readOnly = true,
                enabled = enabled,
                onValueChange = {})
        } else {
            BasicTextField(
                value = code,
                modifier = codeModifier,
                textStyle = TextStyles.codeStyle,
                maxLines = 1000,
                enabled = enabled,
                onValueChange = onChange
            )
        }
    }
}

@Composable
fun codeEditorDialog(
    code: String,
    enabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onChange: (code: String) -> Unit,
    dialogBelowContent: @Composable () -> Unit
) {
    val editorPadding = 23.dp
    dialog(enabled = enabled, dialogContent = {
        Column(Modifier.width(470.dp).height(500.dp)) {
            codeWindow(
                code, modifier = Modifiers.PageModifier.padding(
                    start = editorPadding, end = editorPadding, top = editorPadding
                )
            ) { onChange(it) }
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(start = editorPadding, end = editorPadding),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                outlinedButton("Cancel", onClick = onCancel)
                Spacer(Modifier.width(20.dp))
                filledButton("Save", onClick = onSave)
            }
        }
    }) { dialogBelowContent() }
}

@Composable
fun codeViewerDialog(code: String, enabled: Boolean, onClose: () -> Unit, dialogBelowContent: @Composable () -> Unit) {
    val editorPadding = 23.dp
    dialog(enabled = enabled, dialogContent = {
        Column(Modifier.width(700.dp).height(500.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 14.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) { closeButton { onClose() } }
            codeWindow(
                code, readOnly = true, modifier = Modifier.padding(
                    start = editorPadding, end = editorPadding, bottom = editorPadding, top = 5.dp
                ).fillMaxSize()
            )
        }
    }) { dialogBelowContent() }
}

