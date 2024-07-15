package com.example.librarys

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

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
    }
}
