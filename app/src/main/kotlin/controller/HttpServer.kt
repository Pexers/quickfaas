/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package controller

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import model.CloudCompanion
import model.authentication.CloudKeyStore.loadKeyStore
import model.authentication.SessionData
import model.authentication.authRoutes

private const val API_DOMAIN = "http://localhost"
private const val API_PORT: Int = 8080
private const val API_BASE_PATH = "quickfaas"
const val API_SCHEME_AUTHORITY = "$API_DOMAIN:$API_PORT/$API_BASE_PATH"

fun startHttpServer(cloudProviders: Array<CloudCompanion>) = embeddedServer(Netty, port = API_PORT) {
    install(Authentication) {
        loadKeyStore()  // Load OAuth secrets
        cloudProviders.forEach { cc ->
            oauth(cc.cloudAuth.configName) {
                urlProvider = { "$API_DOMAIN:$API_PORT/$API_BASE_PATH/${cc.shortName}/callback" }
                providerLookup = { cc.cloudAuth.getOAuthSettings() }
                client = httpClient
            }
        }
    }
    install(Sessions) { cookie<SessionData>("user_session") }
    // Register API routes
    cloudProviders.forEach { cc -> this.routing { authRoutes(API_BASE_PATH, cc) } }
}.start(wait = false)
