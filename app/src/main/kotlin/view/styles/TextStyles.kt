/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.styles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

object TextStyles {

    val smallStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.4.sp
    )

    val mediumStyle = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 15.sp,
        letterSpacing = 0.25.sp
    )

    val bigStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        letterSpacing = 0.9.sp
    )

    val veryBigStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        letterSpacing = 0.20.sp
    )

    val codeStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        letterSpacing = 0.20.sp
    )

    val urlStyle = TextStyle(
        color = Color(0xFF0645AD),
        textDecoration = TextDecoration.Underline,
        fontStyle = FontStyle.Italic,
        fontSize = 15.sp,
        letterSpacing = 0.25.sp
    )

}