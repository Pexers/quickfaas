/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.authencation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.CloudCompanion
import model.Utils.IMAGES
import model.authentication.openAuthWebPage
import view.common.roundedButton
import view.styles.Modifiers
import view.styles.TextStyles

private val cloudCardWidth = 160.dp
private val cloudCardHeight = 250.dp

data class CloudViewData(
    val colors: Colors, val logoSize: Dp
)

@Composable
fun cloudCard(cc: CloudCompanion, isSignedIn: Boolean, onChangeCC: (cc: CloudCompanion) -> Unit) {
    val cloudView: CloudViewData = cc.cloudViewData
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.width(cloudCardWidth).height(cloudCardHeight),
        backgroundColor = cloudView.colors.primary,
        shape = Modifiers.cardShape,
        elevation = 10.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            cloudLogo("${cc.shortName}-logo.png", cloudView.logoSize)
            Text(
                text = cc.name,
                modifier = Modifier.width(cloudCardWidth - 20.dp).padding(top = 13.dp),
                style = TextStyles.veryBigStyle,
                textAlign = TextAlign.Center,
                color = Color.White,
            )
            Column(
                modifier = Modifier.fillMaxHeight().padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!isSignedIn) {
                    roundedButton("Sign in", contentColor = cloudView.colors.onPrimary, alpha = 0.85f) {
                        scope.launch { withContext(Dispatchers.IO) { openAuthWebPage(cc.shortName) } }
                    }
                } else {
                    Text(
                        text = "Already signed in",
                        style = TextStyles.mediumStyle,
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                    roundedButton("Create FaaS", contentColor = cloudView.colors.onPrimary) { onChangeCC(cc) }
                }
            }
        }
    }
}

@Composable
fun cloudLogo(logoFile: String, size: Dp) {
    val circleSize: Dp = 80.dp
    val backgroundColor = MaterialTheme.colors.background
    Box(modifier = Modifier.padding(top = 20.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.shadow(12.dp, CircleShape).size(circleSize, circleSize))
        Canvas(modifier = Modifier.size(circleSize), onDraw = { drawCircle(color = backgroundColor) })
        Image(
            painterResource("$IMAGES/$logoFile"),
            contentDescription = "cloud-logo",
            modifier = Modifier.requiredSize(size)
        )
    }
}
