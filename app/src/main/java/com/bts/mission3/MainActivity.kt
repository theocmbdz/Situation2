package com.bts.mission3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSalles = findViewById<Button>(R.id.btn_salles)

        btnSalles.setOnClickListener {
            val intent = Intent(this, Salle::class.java)
            startActivity(intent)
        }
    }
}
