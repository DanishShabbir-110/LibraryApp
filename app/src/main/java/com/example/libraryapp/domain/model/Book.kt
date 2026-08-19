package com.example.libraryapp.domain.model

data class Book(
    val id: Int = 0,
    val name: String,
    val author: String,
    val category: String,
    val totalCopies: Int,
    val availableCopies: Int
)
