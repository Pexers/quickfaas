/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model

import androidx.compose.ui.unit.dp
import model.authentication.GcpAuth
import model.projects.GcpProject
import model.projects.ProjectData
import model.requests.GcpRequests
import view.menus.creation.authencation.CloudViewData
import view.styles.AppColors.GCPColors

class GcpProvider : CloudProvider {

    companion object : CloudCompanion {
        override val name = "Google Cloud Platform"
        override val shortName = "gcp"
        override val cloudAuth = GcpAuth
        override val cloudRequests = GcpRequests
        override val cloudViewData = CloudViewData(colors = GCPColors, logoSize = 58.dp)
        override fun newCloudProvider() = GcpProvider()
    }

    override val companion = Companion
    override var projects: List<ProjectData> = listOf()
    override val project = GcpProject()
    override val cloudSpecifics = null

    override suspend fun requestProjects(): List<ProjectData> {
        project.projectData.name = ""
        projects = GcpRequests.getProjects().projects
        return projects
    }

}
