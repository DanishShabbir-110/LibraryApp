package com.example.libraryapp.domain.cases.borrowbooks

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class ReturnBookUseCase @Inject constructor(
    private val borrowBookRepository: BorrowBookRepository,
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Int,memberId:Int,returnDate: Long){
        val activeBorrowBook=borrowBookRepository.getActiveBorrowBook(
            bookId,
            memberId
        )

        if(activeBorrowBook==null){
            throw IllegalStateException(
                "No active borrowing found for this book and member"
            )
        }

        borrowBookRepository.returnBook(
            activeBorrowBook.id,
            returnDate
        )
        bookRepository.increaseAvailableCopies(bookId)
    }
}