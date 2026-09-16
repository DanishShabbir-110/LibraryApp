package com.example.libraryapp.domain.model

enum class BookCategory {
    PROGRAMMING,
    DATABASE,
    RESEARCH;

    fun displayName(): String {
        return name.lowercase().replaceFirstChar { it.uppercase() }
    }
}