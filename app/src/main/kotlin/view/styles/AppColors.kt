/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.styles

import androidx.compose.material.TopAppBar
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

/**
 * @property primary The primary color is the color displayed most frequently across your app’s
 * screens and components.
 * @property primaryVariant The primary variant color is used to distinguish two elements of the
 * app using the primary color, such as the top app bar and the system bar.
 * @property secondary The secondary color provides more ways to accent and distinguish your
 * product. Secondary colors are best for:
 * - Floating action buttons
 * - Selection controls, like checkboxes and radio buttons
 * - Highlighting selected text
 * - Links and headlines
 * @property secondaryVariant The secondary variant color is used to distinguish two elements of the
 * app using the secondary color.
 * @property background The background color appears behind scrollable content.
 * @property surface The surface color is used on surfaces of components, such as cards, sheets and
 * menus.
 * @property error The error color is used to indicate error within components, such as text fields.
 * @property onPrimary Color used for text and icons displayed on top of the primary color.
 * @property onSecondary Color used for text and icons displayed on top of the secondary color.
 * @property onBackground Color used for text and icons displayed on top of the background color.
 * @property onSurface Color used for text and icons displayed on top of the surface color.
 * @property onError Color used for text and icons displayed on top of the error color.
 * @property isLight Whether this Colors is considered as a 'light' or 'dark' set of colors. This
 * affects default behavior for some components: for example, in a light theme a [TopAppBar] will
 * use [primary] by default for its background color, when in a dark theme it will use [surface].
 */

object AppColors {
    private val WHITE_LIGHT = Color(0xFFF7F7F7)
    private val WHITE_MEDIUM = Color(0xFFEDF2F4)
    private val WHITE_DARK = Color(0xFFDEE2E6)
    private val GRAY_LIGHT = Color(0xFFE2E7E9)
    private val RED_LIGHT = Color(0xFFf94144)
    private val BLUE_LIGHT = Color(0xFFD0DEEE)
    private val BLUE_MEDIUM = Color(0xFF59C6FF)
    private val BLUE_DARK = Color(0xFF0077B5)
    private val BLUE_MSAZURE = Color(0xFF00A4EF)
    private val BLUE_DARK_MSAZURE = Color(0xFF0087C4)
    private val RED_GCP = Color(0xFFDB4437)
    private val RED_DARK_GCP = Color(0xFFC84035)
    private val ORANGE_AWS = Color(0xFFFF9900)


    val MainColors = lightColors(
        primary = BLUE_DARK, primaryVariant = BLUE_MEDIUM, secondary = WHITE_MEDIUM, secondaryVariant = WHITE_DARK,
        background = WHITE_LIGHT, onError = RED_LIGHT
    )

    val MenuColors = lightColors(
        primary = GRAY_LIGHT, primaryVariant = BLUE_LIGHT
    )

    val FieldColors = lightColors(
        primary = BLUE_DARK,
        primaryVariant = BLUE_MEDIUM,
        onPrimary = Color.Black,
        background = WHITE_MEDIUM,
        onBackground = Color.Gray,
        onError = RED_LIGHT
    )

    val MsAzureColors = lightColors(
        primary = BLUE_MSAZURE,
        onPrimary = BLUE_DARK_MSAZURE,
    )

    val GCPColors = lightColors(
        primary = RED_GCP,
        onPrimary = RED_DARK_GCP,
    )

    val AWSColors = lightColors(
        primary = ORANGE_AWS, onPrimary = Color.Black
    )

}