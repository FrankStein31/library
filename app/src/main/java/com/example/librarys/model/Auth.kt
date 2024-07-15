package com.example.librarys.model

data class LoginData(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Int,
    val message: String,
    val data: UserData?
)

data class RegisterData(
    val name: String,
    val email: String,
    val password: String,
    val nis: String,
    val user_class: Int,
)

data class RegisterResponse(
    val status: Int,
    val message: String,
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val nis: String,
    val `class`: Int,
    val role: String,
    val accessToken: String,
    val isDeleted: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)