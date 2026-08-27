package com.example.libraryapp.domain.repository

import com.example.libraryapp.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>

    suspend fun getBookById(id: Int): Book?

    suspend fun addBook(book: Book)
    suspend fun updateBook(book: Book)
    suspend fun deleteBook(book: Book)

    suspend fun decreaseAvailableCopies(
        bookId: Int
    )


    suspend fun increaseAvailableCopies(
        bookId: Int
    )
}