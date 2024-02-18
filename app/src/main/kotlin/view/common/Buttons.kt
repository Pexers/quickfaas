/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import view.styles.AppColors.MenuColors
import view.styles.Modifiers
import view.styles.TextStyles

@Composable
fun filledButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(shape = Modifiers.buttonShape, enabled = enabled, onClick = onClick) { Text(text) }
}

@Composable
fun outlinedButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    OutlinedButton(
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        enabled = enabled,
        shape = Modifiers.buttonShape,
        onClick = onClick
    ) { Text(text) }
}

@Composable
fun rectangleButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = Modifier.height(28.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MenuColors.primary),
        shape = RectangleShape,
        enabled = enabled,
        contentPadding = PaddingValues(start = 13.dp, end = 13.dp),
        onClick = onClick
    ) { Text(text, style = TextStyles.smallStyle, fontWeight = FontWeight.Medium) }
}

@Composable
fun iconButton(text: String, imgPath: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        contentPadding = PaddingValues(7.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
        enabled = enabled,
        shape = Modifiers.buttonShape,
        onClick = onClick
    ) {
        Image(painterResource(imgPath), modifier = Modifier.requiredSize(18.dp), contentDescription = "Image: $imgPath")
        Text(text, modifier = Modifier.padding(PaddingValues(start = 7.dp)), style = TextStyles.smallStyle)
    }
}

@Composable
fun closeButton(enabled: Boolean = true, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.size(35.dp),
        border = BorderStroke(0.dp, Color.Unspecified),
        contentPadding = PaddingValues(3.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        enabled = enabled,
        shape = CircleShape,
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close button")
    }
}

@Composable
fun roundedButton(text: String, contentColor: Color, alpha: Float = 1f, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(contentColor = contentColor, backgroundColor = Color.White.copy(alpha)),
        shape = Modifiers.buttonRoundedShape,
        elevation = null,
        enabled = enabled,
        onClick = onClick
    ) { Text(text) }
}

@Composable
fun roundedBigButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(140.dp).height(40.dp),
        colors = ButtonDefaults.buttonColors(contentColor = Color.White, backgroundColor = Color(0xFF264653)),
        shape = Modifiers.buttonRoundedShape,
        enabled = enabled,
        onClick = onClick
    ) { Text(text, style = TextStyles.bigStyle) }
}

@Composable
fun textButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.height(28.dp),
        shape = RectangleShape,
        enabled = enabled,
        contentPadding = PaddingValues(5.dp),
        onClick = onClick
    ) { Text(text, style = TextStyles.mediumStyle) }
}

@Composable
fun textBigButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.width(80.dp).height(40.dp),
        shape = Modifiers.buttonRoundedShape,
        enabled = enabled,
        contentPadding = PaddingValues(5.dp),
        onClick = onClick
    ) { Text(text, style = TextStyles.bigStyle) }
}