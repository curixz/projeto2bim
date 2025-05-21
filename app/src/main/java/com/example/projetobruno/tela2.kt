package com.example.projetobruno

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projetobruno.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class tela2 : AppCompatActivity() {

    private val client = OkHttpClient()
    private val apiKey = "AIzaSyD-FVyC22lGD39x2IIwadpZfNP0EWSsDgI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela2)

        val btnDesculpas = findViewById<Button>(R.id.btnDesculpas)
        val btnPresente = findViewById<Button>(R.id.btnPresente)
        val textviewDesculpas2 = findViewById<TextView>(R.id.textViewDesculpas2)
        val textviewPresente2 = findViewById<TextView>(R.id.textviewPresente2)

        btnDesculpas.setOnClickListener {
            val prompt = "Crie um pedido de desculpas para uma namorada ou namorado no máximo 3 linhas. Comece com Amor, errei fui moleque"
            enviarPerguntaGemini(prompt) { resposta ->
                runOnUiThread { textviewDesculpas2.text = resposta }
            }
        }

        btnPresente.setOnClickListener {
            val prompt = "Sugira um presente criativo para pedir desculpas para namorada ou namorado. Diga apenas o presente mais nada"
            enviarPerguntaGemini(prompt) { resposta ->
                runOnUiThread { textviewPresente2.text = resposta }
            }
        }
    }

    private fun enviarPerguntaGemini(pergunta: String, callback: (String) -> Unit) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val json = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", pergunta)
                ))
            ))
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Erro: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val resposta = response.body?.string() ?: "Sem resposta"
                try {
                    val jsonResposta = JSONObject(resposta)
                    val texto = jsonResposta
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    callback(texto)
                } catch (e: Exception) {
                    callback("Erro ao ler resposta: ${e.message}")
                }
            }
        })
    }
}
