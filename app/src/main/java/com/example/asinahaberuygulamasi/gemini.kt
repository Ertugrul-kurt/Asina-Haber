package com.example.asinahaberuygulamasi

import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers

object GeminiService {
    private const val API_KEY = BuildConfig.GEMINI_API_KEY
    private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"

    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("$API_URL?key=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            connection.outputStream.use { it.write(body.toString().toByteArray()) }

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            json.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } catch (e: Exception) {
            "Hata: ${e.message}"
        }
    }

    suspend fun haberDogrula(haberBaslik: String, haberIcerik: String): String =
        generateContent(
            "Bu haberin doğruluğunu analiz et ve kısa bir değerlendirme yap:\n\nBaşlık: $haberBaslik\n\nİçerik: $haberIcerik"
        )

    suspend fun haberOzetle(haberBaslik: String, haberIcerik: String): String =
        generateContent(
            "Aşağıdaki haberi 3-4 cümleyle özetle:\n\nBaşlık: $haberBaslik\n\nİçerik: $haberIcerik"
        )

    suspend fun haberSoruCevap(
        haberBaslik: String,
        haberIcerigi: String,
        kullaniciSorusu: String
    ): String =
        generateContent(
            "Aşağıdaki haber hakkında sorulan soruyu yanıtla:\n\nBaşlık: $haberBaslik\n\nİçerik: $haberIcerigi\n\nSoru: $kullaniciSorusu"
        )
}