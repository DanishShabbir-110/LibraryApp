package com.example.libraryapp.domain.usecases.books

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.repository.BookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: Book)=repository.deleteBook(book)
}