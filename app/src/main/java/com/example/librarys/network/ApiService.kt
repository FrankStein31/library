package com.example.librarys.network

import com.example.librarys.model.Book
import com.example.librarys.model.BookResponse
import com.example.librarys.model.LoginData
import com.example.librarys.model.LoginResponse
import com.example.librarys.model.RegisterData
import com.example.librarys.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    fun login(@Header("lat") lat: Double, @Header("long") long: Double, @Body loginData: LoginData): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Header("lat") lat: Double, @Header("long") long: Double, @Body registerData: RegisterData): Call<RegisterResponse>

    @GET("api/book/category/{categoryId}")
    fun getBooksByCategory(@Header("lat") lat: Double, @Header("long") long: Double, @Path("categoryId") categoryId: Int): Call<BookResponse>

    @GET("api/book/{id}")
    fun getBooksById(@Path("id") int: Int): Call<BookResponse>

}