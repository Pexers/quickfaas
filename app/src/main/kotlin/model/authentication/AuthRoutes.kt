/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.authentication

import controller.API_SCHEME_AUTHORITY
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import model.CloudCompanion
import model.Utils

data class SessionData(var token: String = "")

fun openAuthWebPage(cpShortName: String) = Utils.openWebPage("$API_SCHEME_AUTHORITY/$cpShortName/login")

fun Routing.authRoutes(apiBasePath: String, cc: CloudCompanion) {
    val cpShortName = cc.shortName
    route("/$apiBasePath/$cpShortName") {
        authenticate(cc.cloudAuth.configName) {
            get("/login") { /** Redirects to 'authorizeUrl' automatically **/ }
            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
                call.sessions.set(SessionData(principal?.accessToken.toString()))
                call.respondRedirect("/$apiBasePath/$cpShortName/auth-status")
            }
        }
        get("/auth-status") {
            val session: SessionData? = call.sessions.get()
            if (session != null) {
                cc.cloudAuth.session = session
                cc.cloudRequests.setBearerToken(session.token)
                TokenListeners.notifyTokenListeners(cpShortName)
                call.respondText("Successfully authenticated in ${cc.name}", status = HttpStatusCode.OK)
            } else {
                call.respondText(
                    "Something went wrong. Please try again later.",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
