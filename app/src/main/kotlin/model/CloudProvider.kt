/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model

import model.projects.CloudProject
import model.projects.ProjectData
import model.specifics.CloudSpecifics

interface CloudProvider {
    val companion: CloudCompanion
    var projects: List<ProjectData>
    val project: CloudProject
    val cloudSpecifics: CloudSpecifics?

    suspend fun requestProjects(): List<ProjectData>
    fun setProjectData(projectIdx: Int) {
        project.projectData = projects[projectIdx]
        project.function.bucket.bucketData.name = ""  // Selected bucket depends on the selected project
    }
}
