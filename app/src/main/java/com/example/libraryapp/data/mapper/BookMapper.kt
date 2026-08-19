package com.example.libraryapp.data.mapper

import com.example.libraryapp.data.local.models.BookEntity
import com.example.libraryapp.domain.model.Book

fun BookEntity.toDomain(): Book {
    return Book(
        id=id,
        name=name,
        author = author,
        category=category,
        totalCopies = totalCopies,
        availableCopies = availableCopies
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id=id,
        name=name,
        author = author,
        category=category,
        totalCopies = totalCopies,
        availableCopies = availableCopies
    )
}