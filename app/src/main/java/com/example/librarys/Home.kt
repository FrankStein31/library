package com.example.librarys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnScanQR = findViewById<Button>(R.id.btnScan)
        btnScanQR.setOnClickListener {
            startQRCodeScanner()
        }
    }

    private fun startQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val scannedData = result.contents
                handleScannedData(scannedData)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleScannedData(scannedData: String) {
        val qrCodeData = scannedData
        val intent = Intent(this, Book::class.java)
        intent.putExtra("scanned_data", qrCodeData)
        startActivity(intent)
    }
}