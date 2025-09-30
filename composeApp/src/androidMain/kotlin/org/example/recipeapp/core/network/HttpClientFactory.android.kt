package org.example.recipeapp.core.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual class HttpClientFactory {
    actual fun create(): HttpClient {
        val engine = HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
            }
        }
        return createJsonHttpClient(engine)
    }
}