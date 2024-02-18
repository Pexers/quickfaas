/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.deployment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import controller.General.AUTO_DELETE_FUNC_ZIP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.CloudProvider
import model.Utils
import model.Utils.openWebPage
import view.common.circularProgress
import view.common.roundedBigButton
import view.common.textBigButton
import view.menus.creation.development.codeViewerDialog
import view.menus.creation.development.hookFunctionPage
import view.navigation.PageNavigation
import view.navigation.navigationButtons
import view.styles.AppColors
import view.styles.Modifiers
import view.styles.Modifiers.PageModifier
import view.styles.TextStyles
import java.io.File

// @formatter:off
private enum class DeploymentStatus(val msg: String, val color: Color, val img: String) {
    IN_PROGRESS(msg = "", Color.Transparent, ""),
    SUCCESS(msg = "Deployment finished successfully!", Color(0xFF188038), "check.png"),
    ERROR(msg = "Deployment failed\nPlease check the logs", AppColors.MainColors.onError, "error.png"),
    NONE(msg = "", Color.Transparent, "") }
// @formatter:on

@Composable
fun deploymentPage(cp: CloudProvider, pageNav: PageNavigation) {
    val projFunc = cp.project.function
    var deploymentStatus by remember { mutableStateOf(DeploymentStatus.NONE) }
    var deploymentMsg by remember { mutableStateOf("") }
    var logsEnabler by remember { mutableStateOf(false) }
    var logs by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val setDeploymentMsg: (msg: String) -> Unit = { msg -> deploymentMsg = "$msg..." }

    codeViewerDialog(code = logs, logsEnabler, onClose = { logsEnabler = false }) {
        Column {
            Column(PageModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.fillMaxHeight(0.1f))
                Card(
                    modifier = Modifier.width(280.dp).height(280.dp),
                    backgroundColor = Color.White,
                    shape = Modifiers.cardShape,
                    elevation = Modifiers.cardElevation
                ) {
                    Column(modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Summary",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                        functionSummary(
                            arrayOf(
                                Pair("Name", projFunc.name),
                                Pair("Location", projFunc.location),
                                Pair("Trigger", projFunc.trigger.name),
                                Pair("Runtime",
                                    projFunc.runtimeVersion!!.let { rv -> "${rv.runtime.runtimeName} ${rv.version}" })
                            )
                        )
                    }
                }
                Row(Modifier.padding(top = 40.dp)) {
                    roundedBigButton("Deploy", deploymentStatus == DeploymentStatus.NONE) {
                        deploymentStatus = DeploymentStatus.IN_PROGRESS
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                var tmpDir: File? = null
                                deploymentStatus = try {
                                    tmpDir = projFunc.buildAndZip(cp.companion.shortName, setDeploymentMsg)
                                    projFunc.deployZip(
                                        "${tmpDir.path}/${Utils.ZIP_FILE}",
                                        cp.project.projectData,
                                        setDeploymentMsg
                                    )
                                    DeploymentStatus.SUCCESS
                                } catch (e: Exception) {
                                    logs = e.message.orEmpty()
                                    e.printStackTrace()
                                    DeploymentStatus.ERROR
                                } finally {
                                    if (AUTO_DELETE_FUNC_ZIP && tmpDir != null && tmpDir.path.isNotEmpty()) tmpDir.deleteRecursively()
                                }
                            }
                        }
                    }
                    if (deploymentStatus == DeploymentStatus.SUCCESS || deploymentStatus == DeploymentStatus.ERROR) {
                        Spacer(Modifier.width(10.dp))
                        textBigButton("Logs") {
                            logsEnabler = true
                        }
                    }
                }
                Row(
                    modifier = Modifier.height(70.dp).padding(top = 35.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val deploymentStateValue = deploymentStatus
                    if (deploymentStateValue != DeploymentStatus.NONE) {
                        if (deploymentStateValue == DeploymentStatus.IN_PROGRESS) {
                            circularProgress(enabled = true, Modifier.padding(end = 10.dp).size(28.dp))
                            Text(text = deploymentMsg, color = MaterialTheme.colors.primary)
                        } else {
                            Image(
                                painterResource("${Utils.IMAGES}/${deploymentStateValue.img}"),
                                modifier = Modifier.padding(end = 10.dp).size(25.dp),
                                contentDescription = "Deployment status"
                            )
                            Text(text = deploymentStateValue.msg, color = deploymentStateValue.color)
                        }
                    }
                }
                if (deploymentStatus == DeploymentStatus.SUCCESS) {
                    val triggerUrlPair = projFunc.getTriggerUrl(cp.project.projectData)
                    ClickableText(modifier = Modifier.padding(top = 10.dp),
                        text = AnnotatedString(triggerUrlPair.first.ifEmpty { triggerUrlPair.second }),
                        style = TextStyles.urlStyle,
                        onClick = { openWebPage(triggerUrlPair.second) })
                    Text(
                        modifier = Modifier.padding(top = 7.dp),
                        text = '(' + projFunc.trigger.postDeploymentMsg + ')',
                        style = TextStyles.mediumStyle
                    )
                }
            }
            navigationButtons(pageNav, previousPage = { hookFunctionPage(cp, pageNav) })
        }
    }
}

private fun previousEnabler() {
    // TODO
}

@Composable
private fun functionSummary(summary: Array<Pair<String, String>>) {
    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
        summary.forEach {
            Row {
                Text(
                    modifier = Modifier.padding(end = 5.dp).fillMaxWidth(0.5f),
                    text = it.first + ':',
                    color = Color.Gray,
                    style = TextStyles.mediumStyle,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.second,
                    color = Color.Gray,
                    style = TextStyles.mediumStyle,
                    fontStyle = FontStyle.Italic,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

