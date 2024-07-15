package com.example.librarys.model


data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val publisher: String,
    val publication_year: Int,
    val category_id: Int,
    val categoryName: String,
    val description_book: String,
    val image: String,
    val pdf_file: String,
    val is_deleted: Int,
)

data class BookResponse(
    val status: Int,
    val message: String,
    val data: List<Book>
)
