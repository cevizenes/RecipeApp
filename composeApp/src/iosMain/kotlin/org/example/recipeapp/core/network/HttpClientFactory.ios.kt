package org.example.recipeapp.core.network

import io.ktor.client.*
import io.ktor.client.engine.darwin.*

actual class HttpClientFactory {
    actual fun create(): HttpClient {
        val engine = HttpClient(Darwin) {
            engine {
                configureRequest {
                    setAllowsCellularAccess(true)
                }
            }
        }
        return createJsonHttpClient(engine)
    }
}