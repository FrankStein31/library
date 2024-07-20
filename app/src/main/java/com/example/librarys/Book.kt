package com.example.librarys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.librarys.model.Book
import com.example.librarys.model.BookResponse
import com.example.librarys.network.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Book : AppCompatActivity() {
    private var userId: String? = null
    private lateinit var listViewBooks: ListView
    private lateinit var booksAdapter: BookAdapter
    private var booksList: List<Book> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val scannedData = intent.getStringExtra("scanned_data") ?: "1" // Default categoryId
        listViewBooks = findViewById(R.id.listViewBooks)
        booksAdapter = BookAdapter(this, booksList)
        listViewBooks.adapter = booksAdapter
        userId = intent.getStringExtra("userId")
        listViewBooks.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedBook = booksList[position]
            val intent = Intent(this@Book, Detail::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("book_id", selectedBook.id)
            startActivity(intent)
        }

        if (checkLocationPermission()) {
            getLastKnownLocation(scannedData.toInt())
        } else {
            requestLocationPermission()
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
                val scannedData = intent.getStringExtra("scanned_data") ?: "1"
                getLastKnownLocation(scannedData.toInt())
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLastKnownLocation(categoryId: Int) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // Use the location object
                        val lat = location.latitude
                        val long = location.longitude

                        // Fetch books by category with location data
                        fetchBooksByCategory(categoryId, lat, long)
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

    private fun fetchBooksByCategory(categoryId: Int, lat: Double, long: Double) {
        val call = RetrofitClient.instance.getBooksByCategory(lat, long, categoryId)
        call.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    val bookResponse = response.body()
                    if (bookResponse != null) {
                        booksList = bookResponse.data
                        booksAdapter.updateData(booksList)
                    } else {
                        Toast.makeText(this@Book, "No books found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Book, "Failed to fetch books", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Toast.makeText(this@Book, "Connection failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
