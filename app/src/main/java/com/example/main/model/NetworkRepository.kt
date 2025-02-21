package com.example.main.model

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkRepository {

    private val client = HttpClient(CIO)

    suspend fun getJsonData(url: String): String = withContext(Dispatchers.IO) {
        client.get(url).bodyAsText()
    }
}