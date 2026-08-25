package com.example.libraryapp.data.mapper

import com.example.libraryapp.data.local.entities.BookEntity
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BookCategory

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        name = name,
        author = author,
        category = BookCategory.entries.find {
            it.name.equals(category, ignoreCase = true)
        } ?: BookCategory.PROGRAMMING,
        totalCopies = totalCopies,
        availableCopies = availableCopies
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        name = name,
        author = author,
        category = category.name.lowercase().replaceFirstChar { char -> char.uppercase() },
        totalCopies = totalCopies,
        availableCopies = availableCopies
    )
}