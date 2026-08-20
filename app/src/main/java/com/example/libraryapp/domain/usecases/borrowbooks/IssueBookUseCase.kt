package com.example.libraryapp.domain.cases.borrowbooks

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class IssueBookUseCase @Inject constructor(
    private val borrowBookRepository: BorrowBookRepository,
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(book: Book, memberId: Int, issueDate: Long) {
        if(book.availableCopies<=0){
            throw IllegalStateException("This book is not available")
        }
        val issueBook= BorrowBook(
            bookId = book.id,
            memberId = memberId,
            issueDate = issueDate,
            returnDate = null,
            status = BorrowingStatus.ISSUED
        )
        val borrowedBook=Book(
            id=book.id,
            name=book.name,
            author = book.author,
            category = book.category,
            totalCopies = book.totalCopies,
            availableCopies = book.availableCopies-1
        )
        borrowBookRepository.issueBook(issueBook)
        bookRepository.updateBook(borrowedBook)
    }
}