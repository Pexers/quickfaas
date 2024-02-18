/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.projects

import model.resources.buckets.BucketData
import model.resources.functions.CloudFunction

interface CloudProject {
    var projectData: ProjectData
    var buckets: List<BucketData>
    val function: CloudFunction

    suspend fun requestBuckets(): List<BucketData>
}
