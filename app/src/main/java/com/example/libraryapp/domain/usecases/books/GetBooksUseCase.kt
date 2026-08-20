package com.example.libraryapp.domain.usecases.books

import com.example.libraryapp.domain.repository.BookRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke() =repository.getAllBooks()
}