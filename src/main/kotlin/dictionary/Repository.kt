package org.example.dictionary

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.example.dictionary.Repository.loadDefinition
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object Repository {

    private const val BASE_URL = "https://api.api-ninjas.com/v1/dictionary?word="
    private const val API_KEY = "TxPZhoetjaQAlxaRgMaS5OLSOej2IyQrY9I9wAqx"
    private const val HEADER_KEY = "X-Api-Key"

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadDefinition(word: String): String {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val urlString = BASE_URL + word
                val url = URL(urlString)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    addRequestProperty(HEADER_KEY, API_KEY)
                }
                val response = connection.inputStream.bufferedReader().readText()
                json.decodeFromString<Definition>(response).definition
            } catch (e: Exception) {
                println(e)
                ""
            } finally {
                connection?.disconnect()
            }
        }
    }
}

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    scope.launch {
        while (true) {
            print("Enter word: ")
            val word = readln()
            val definition = loadDefinition(word)
            println(definition)
        }
    }
}