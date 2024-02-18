/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model

import androidx.compose.ui.unit.dp
import model.authentication.MsAzureAuth
import model.projects.MsAzureProject
import model.projects.MsAzureProjectData
import model.projects.ProjectData
import model.requests.MsAzureRequests
import model.specifics.MsAzureSpecifics
import view.menus.creation.authencation.CloudViewData
import view.styles.AppColors.MsAzureColors

class MsAzureProvider : CloudProvider {

    companion object : CloudCompanion {
        override val name = "Microsoft Azure"
        override val shortName = "msazure"
        override val cloudAuth = MsAzureAuth
        override val cloudRequests = MsAzureRequests
        override val cloudViewData = CloudViewData(colors = MsAzureColors, logoSize = 57.dp)
        override fun newCloudProvider(): CloudProvider = MsAzureProvider()
    }

    override val companion = Companion
    override var projects: List<ProjectData> = listOf()
    override val project = MsAzureProject()
    override val cloudSpecifics = MsAzureSpecifics()

    override suspend fun requestProjects(): List<ProjectData> {
        project.projectData.name = ""
        projects = MsAzureRequests.getResourceGroups(cloudSpecifics.subscription.subscriptionId).value
        return projects
    }

    override fun setProjectData(projectIdx: Int) {
        project.projectData = projects[projectIdx]
        // Save subscription ID in project data
        (project.projectData as MsAzureProjectData).subscriptionId = cloudSpecifics.subscription.subscriptionId
        project.function.bucket.bucketData.name = ""
    }

}
