/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import model.Utils.IMAGES
import view.styles.AppColors
import view.styles.AppColors.MainColors

data class MenuData(val title: String, val icon: String, val content: @Composable () -> Unit)

@Composable
fun menuNavigation(menus: Array<MenuData>) {
    var selectedMenu by remember { mutableStateOf(menus[0]) }
    Row {
        Column(Modifier.fillMaxHeight()) {
            menus.forEach { menu ->
                menuButton(
                    Modifier.width(50.dp).weight(1f),
                    imgPath = "$IMAGES/${menu.icon}",
                    isSelected = selectedMenu == menu
                ) { selectedMenu = menu }
            }
        }
        Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) { selectedMenu.content() }
    }
}

@Composable
private fun menuButton(modifier: Modifier, imgPath: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) AppColors.MenuColors.primaryVariant else AppColors.MenuColors.primary),
        shape = RectangleShape,
        border = BorderStroke(0.dp, MainColors.background),
        onClick = onClick
    ) {
        Image(painterResource(imgPath), contentDescription = "Image: $imgPath", modifier = Modifier.requiredSize(32.dp))
    }
}
