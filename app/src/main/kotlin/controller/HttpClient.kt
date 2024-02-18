/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package controller

import controller.General.HTTP_LOG_LEVEL
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

enum class HttpLogLevel { BASIC, DETAILED, NONE }

private const val HTTP_REQUEST_TIMEOUT: Long = 120000  // 2 min timeout

// Console colors
private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_BLUE = "\u001B[34m"

val httpClient = HttpClient(CIO.create { requestTimeout = HTTP_REQUEST_TIMEOUT }) {
    install(ResponseObserver) {
        onResponse { response ->
            when (HTTP_LOG_LEVEL) {
                HttpLogLevel.BASIC -> println("HttpRequest[${response.request.method.value} $ANSI_BLUE${response.request.url.encodedPathAndQuery}$ANSI_RESET] | HttpResponse[${response.status}]")
                HttpLogLevel.DETAILED -> println("HttpRequest[${response.request.method.value} ${response.request.url}, ContentType=${response.request.content.contentType}] | HttpResponse[${response.status}]")
                HttpLogLevel.NONE -> {}
            }
        }
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}
