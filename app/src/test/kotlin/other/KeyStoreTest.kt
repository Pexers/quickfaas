/*
 * Copyright (c) 7/3/2022, Pexers (https://github.com/Pexers)
 */

package other

import model.CloudCompanion
import model.GcpProvider
import model.MsAzureProvider
import model.authentication.CloudKeyStore

fun main() {
    CloudKeyStore.loadKeyStore()
    /*storeCloudSecrets(
        GcpProvider.Companion, "", ""
    )
    storeCloudSecrets(
        MsAzureProvider.Companion, "", ""
    )*/
    readCloudSecrets(MsAzureProvider)
    readCloudSecrets(GcpProvider)
}

fun storeCloudSecrets(companion: CloudCompanion, clientId: String, clientSecret: String) {
    CloudKeyStore.setEntry("${companion.shortName}_clientId", clientId)
    CloudKeyStore.setEntry("${companion.shortName}_clientSecret", clientSecret)
}

fun readCloudSecrets(companion: CloudCompanion) {
    val clientId = CloudKeyStore.getEntry("${companion.shortName}_clientId")
    val clientSecret = CloudKeyStore.getEntry("${companion.shortName}_clientSecret")
    println("Client ID: $clientId")
    println("Client Secret: $clientSecret")
}

