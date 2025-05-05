package com.bts.mission3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Page2Activity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var datePicker: DatePicker
    private lateinit var textViewCode: TextView
    private lateinit var textViewWifi: TextView

    private var salleId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page2)

        salleId = intent.getIntExtra("salle_id", -1)

        if (salleId == -1) {
            finish() // Salle invalide
            return
        }

        val btnBack = findViewById<Button>(R.id.btn_back)
        val btnValidateDate = findViewById<Button>(R.id.btn_validate_date)
        datePicker = findViewById(R.id.datePicker)
        textViewCode = findViewById(R.id.textViewCode)
        textViewWifi = findViewById(R.id.textViewWifi)

        btnBack.setOnClickListener {
            finish()
        }

        btnValidateDate.setOnClickListener {
            val date = getSelectedDate()
            fetchApiData(date, salleId)
        }
    }

    private fun getSelectedDate(): String {
        val year = datePicker.year
        val month = datePicker.month + 1
        val day = datePicker.dayOfMonth
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    private fun fetchApiData(date: String, salleId: Int) {
        val url = "http://10.0.2.2:8080/edsa-API%203/infos_jour.php?date=$date&salle_id=$salleId"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    textViewCode.text = "Erreur réseau: ${e.message}"
                    textViewWifi.text = "Impossible de récupérer les données."
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val jsonObject = JSONObject(responseBody)
                            val codeEntree = jsonObject.optString("code_entree", null)
                            val cleWifi = jsonObject.optString("cle_wifi", null)
                            val reservations = jsonObject.optJSONArray("reservations")

                            runOnUiThread {
                                // Vérification si les réservations sont vides ou nulles
                                if (reservations != null && reservations.length() > 0) {
                                    if (codeEntree != null && cleWifi != null) {
                                        textViewCode.text = "Code entrée : $codeEntree"
                                        textViewWifi.text = "Clé Wifi : $cleWifi"
                                    } else {
                                        textViewCode.text = "Aucune réservation effectuée."
                                        textViewWifi.text = "Pas de code Wi-Fi disponible."
                                    }
                                } else {
                                     // Si aucune réservation
                                    textViewCode.text = "Aucune réservation effectuée pour cette date."
                                    textViewWifi.text = "Pas de code Wi-Fi disponible."
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                textViewCode.text = "Erreur parsing JSON"
                                textViewWifi.text = "Données indisponibles."
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        textViewCode.text = "Erreur serveur: ${response.code}"
                        textViewWifi.text = "Erreur lors de la récupération."
                    }
                }
            }
        })
    }
}
