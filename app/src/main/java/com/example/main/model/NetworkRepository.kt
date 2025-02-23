package com.example.main.model

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkRepository {

    private val client = HttpClient(CIO)

    suspend fun getJsonData(url: String): String = withContext(Dispatchers.IO) {
        client.get(url).bodyAsText()
    }
}