/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.authentication

import io.ktor.server.auth.OAuthServerSettings.*

interface CloudAuth {
    val configName: String
    var session: SessionData

    fun getOAuthSettings(): OAuth2ServerSettings
    fun isSignedIn(): Boolean = session.token.isNotEmpty()
}
