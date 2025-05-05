package com.bts.mission3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class Salle : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var salleContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salle)

        salleContainer = findViewById(R.id.salleContainer)

        // Charger les salles depuis l'API
        fetchSalles()

        // Bouton retour vers MainActivity
        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            // Retour à MainActivity
            val intent = Intent(this@Salle, MainActivity::class.java)
            startActivity(intent)
            finish() // Facultatif, pour s'assurer que l'activité Salle est fermée
        }
    }

    private fun fetchSalles() {
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/edsa-API%203/salles.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Salle, "Erreur de chargement des salles", Toast.LENGTH_SHORT).show()
                }
                Log.e("SalleActivity", "Erreur: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val sallesArray = JSONArray(body)
                        runOnUiThread {
                            for (i in 0 until sallesArray.length()) {
                                val salle = sallesArray.getJSONObject(i)
                                val salleId = salle.getInt("id")
                                val salleNom = salle.getString("salle_nom")

                                val button = Button(this@Salle).apply {
                                    text = salleNom
                                    layoutParams = LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    setOnClickListener {
                                        // Aller vers Page2Activity avec l'ID de la salle
                                        val intent = Intent(this@Salle, Page2Activity::class.java)
                                        intent.putExtra("salle_id", salleId)
                                        startActivity(intent)
                                    }
                                }
                                salleContainer.addView(button)
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@Salle, "Erreur de parsing JSON", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("SalleActivity", "Parsing error: ${e.message}")
                    }
                }
            }
        })
    }
}
