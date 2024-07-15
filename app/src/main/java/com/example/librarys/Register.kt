package com.example.librarys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.librarys.model.RegisterData
import com.example.librarys.model.RegisterResponse
import com.example.librarys.network.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etNis = findViewById<EditText>(R.id.etNis)
        val etKelas = findViewById<EditText>(R.id.etKelas)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val nis = etNis.text.toString().trim()
            val kelas = etKelas.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && nis.isNotEmpty() && kelas.isNotEmpty()) {
                val userClass = kelas.toIntOrNull()
                if (userClass != null) {
                    if (checkLocationPermission()) {
                        getLastKnownLocation(name, email, password, nis, userClass)
                    } else {
                        requestLocationPermission()
                    }
                } else {
                    Toast.makeText(this, "Class must be a number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please complete the registration data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Anda perlu panggil getLastKnownLocation lagi setelah izin diberikan
                val etName = findViewById<EditText>(R.id.etName)
                val etEmail = findViewById<EditText>(R.id.etEmail)
                val etPassword = findViewById<EditText>(R.id.etPassword)
                val etNis = findViewById<EditText>(R.id.etNis)
                val etKelas = findViewById<EditText>(R.id.etKelas)

                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val nis = etNis.text.toString().trim()
                val kelas = etKelas.text.toString().trim()
                val userClass = kelas.toIntOrNull()

                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && nis.isNotEmpty() && userClass != null) {
                    getLastKnownLocation(name, email, password, nis, userClass)
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLastKnownLocation(name: String, email: String, password: String, nis: String, userClass: Int) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // Use the location object
                        val lat = location.latitude
                        val long = location.longitude

                        // Call your register function with location data
                        register(name, email, password, nis, userClass, lat, long)
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            requestLocationPermission()
        }
    }

    private fun register(name: String, email: String, password: String, nis: String, userClass: Int, lat: Double, long: Double) {
        val registerData = RegisterData(name, email, password, nis, userClass)
        val call = RetrofitClient.instance.register(lat, long, registerData)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    registerResponse?.let {
                        val message = registerResponse.message
                        Toast.makeText(this@Register, message, Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Register, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: "Unknown error"
                    Toast.makeText(this@Register, "Register failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                println("Registration error: ${t.message}")
                Toast.makeText(this@Register, "Registration error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
