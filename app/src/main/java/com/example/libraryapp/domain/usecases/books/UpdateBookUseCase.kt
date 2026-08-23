package com.example.libraryapp.domain.usecases.books

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val borrowBookRepository: BorrowBookRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.updateBook(book)
        borrowBookRepository.updateBorrowBookName(book.name,book.id)
    }
}