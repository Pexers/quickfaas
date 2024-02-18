/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model

import model.authentication.CloudAuth
import model.requests.CloudRequests
import view.menus.creation.authencation.CloudViewData

interface CloudCompanion {
    val name: String
    val shortName: String
    val cloudAuth: CloudAuth
    val cloudRequests: CloudRequests
    val cloudViewData: CloudViewData

    fun newCloudProvider(): CloudProvider
}
