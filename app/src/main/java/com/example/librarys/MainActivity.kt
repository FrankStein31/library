package com.example.librarys

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val lewati = findViewById<TextView>(R.id.lewati)

        btnLogin.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        lewati.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }
}
