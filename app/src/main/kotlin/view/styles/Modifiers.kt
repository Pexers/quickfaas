/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.styles

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Modifiers {

    val PageModifier = Modifier.fillMaxHeight(0.88f).fillMaxWidth()
    val NavButtonsModifier = Modifier.fillMaxHeight().fillMaxWidth(0.93f)

    /* Shapes */
    val buttonShape = RoundedCornerShape(10)
    val buttonRoundedShape = RoundedCornerShape(50)
    val cardShape = RoundedCornerShape(8.dp)

    /* Elevations */
    val cardElevation = 5.dp

}