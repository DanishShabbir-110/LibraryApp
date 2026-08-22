package com.example.libraryapp.domain.cases.borrowbooks

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class IssueBookUseCase @Inject constructor(
    private val borrowBookRepository: BorrowBookRepository,
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(book: Book, member: Member, issueDate: Long) {
        if(book.availableCopies<=0){
            throw IllegalStateException("This book is not available")
        }
        val issueBook= BorrowBook(
            bookId = book.id,
            memberId = member.id,
            issueDate = issueDate,
            memberName = member.name,
            bookName = book.name,
            returnDate = null,
            status = BorrowingStatus.ISSUED
        )
        borrowBookRepository.issueBook(issueBook)
        bookRepository.decreaseAvailableCopies(book.id)
    }
}