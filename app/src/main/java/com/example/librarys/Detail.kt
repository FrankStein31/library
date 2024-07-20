package com.example.librarys

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.librarys.model.Book
import com.example.librarys.model.BookResponse
import com.example.librarys.network.RetrofitClient
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class Detail : AppCompatActivity() {

    private lateinit var imageViewBook: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewAuthor: TextView
    private lateinit var textViewPublisher: TextView
    private lateinit var textViewYear: TextView
    private lateinit var textViewCategory: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var buttonRead: Button
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imageViewBook = findViewById(R.id.imageViewBook)
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewAuthor = findViewById(R.id.textViewAuthor)
        textViewPublisher = findViewById(R.id.textViewPublisher)
        textViewYear = findViewById(R.id.textViewYear)
        textViewCategory = findViewById(R.id.textViewCategory)
        textViewDescription = findViewById(R.id.textViewDescription)
        buttonRead = findViewById(R.id.buttonRead)

        userId = intent.getStringExtra("userId")

        val bookId = intent.getIntExtra("book_id", 0)
        fetchBookDetail(bookId)
    }

    private fun fetchBookDetail(bookId: Int) {
        val call = RetrofitClient.instance.getBooksById(bookId)
        call.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    val book = response.body()
                    if (book != null) {
                        updateUI(book.data[0])
                    }
                } else {
                    // Handle error jika respons tidak sukses
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                // Handle kegagalan koneksi atau permintaan
            }
        })
    }

    private fun updateUI(book: Book) {
        textViewTitle.text = book.title
        textViewAuthor.text = "Author: ${book.author}"
        textViewPublisher.text = "Publisher: ${book.publisher}"
        textViewYear.text = "Tahun Terbit: ${book.publication_year}"
        textViewCategory.text = "Kategori: ${book.categoryName}"
        textViewDescription.text = "Dekripsi : ${book.description_book}"

        val imageUrl = "http://192.168.0.56:3001" + book.image

        Picasso.get()
            .load(imageUrl)
            .into(imageViewBook)

        // Menangani klik tombol "Baca"
//        buttonRead.setOnClickListener {
//            openPdfWithExternalApp(Uri.parse("http://192.168.0.56:3001" + book.pdf_file))
//        }
        buttonRead.setOnClickListener {
            if (userId != null) {
                openPdfWithExternalApp(Uri.parse("http://192.168.0.56:3001" + book.pdf_file))
            } else {
                // Jika belum login, arahkan ke halaman Login
                val intent = Intent(this@Detail, Login::class.java)
                intent.putExtra("returnToDetail", true)
                intent.putExtra("bookId", book.id)
                startActivity(intent)
                finish()  // Tutup activity Detail saat ini
            }
        }

    }

    private fun openPdfWithExternalApp(pdfUrl: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUrl, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@Detail, "Tidak ada aplikasi pembaca PDF yang tersedia", Toast.LENGTH_LONG).show()
        }
    }
}
