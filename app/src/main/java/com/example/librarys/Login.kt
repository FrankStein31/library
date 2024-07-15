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
import com.example.librarys.model.LoginData
import com.example.librarys.model.LoginResponse
import com.example.librarys.network.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                if (checkLocationPermission()) {
                    getLastKnownLocation(email, password)
                } else {
                    requestLocationPermission()
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
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
                val etEmail = findViewById<EditText>(R.id.etEmail)
                val etPassword = findViewById<EditText>(R.id.etPassword)
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                getLastKnownLocation(email, password)
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLastKnownLocation(email: String, password: String) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // Use the location object
                        val lat = location.latitude
                        val long = location.longitude

                        // Call your login function with location data
                        login(email, password, lat, long)
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

    private fun login(email: String, password: String, lat: Double, long: Double) {
        val loginData = LoginData(email, password)
        val call = RetrofitClient.instance.login(lat, long, loginData)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        if (it.data?.role == "student") {
                            val message = "Login success"
                            Toast.makeText(this@Login, message, Toast.LENGTH_SHORT).show()

                            // Redirect to HomeActivity and pass data if needed
                            val intent = Intent(this@Login, Home::class.java)
                            intent.putExtra("userId", it.data.id)
                            intent.putExtra("userName", it.data.name)
                            intent.putExtra("userEmail", it.data.email)
                            intent.putExtra("userRole", it.data.role)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Login, "User not activation, please contact admin!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val message = "email or password is wrong"
                    Toast.makeText(this@Login, "Login failed: $message", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@Login, "Connection failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
