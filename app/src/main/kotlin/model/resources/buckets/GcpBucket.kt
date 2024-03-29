/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.buckets

import kotlinx.serialization.Serializable
import model.requests.GcpRequests
import model.resources.functions.CloudFunction

@Serializable
data class GcpBucketsData(val items: List<GcpBucketData>, val nextPageToken: String = "")

@Serializable
data class GcpBucketData(override var name: String = "") : BucketData

class GcpBucket : CloudBucket {
    override var bucketData: BucketData = GcpBucketData()

    suspend fun uploadToBucket(zipFilePath: String, function: CloudFunction) = GcpRequests.uploadZipToBucket(
        GcpRequests.getSessionUri(bucketData.name, function.name, zipFilePath.substringAfterLast('/')), zipFilePath
    )

}
