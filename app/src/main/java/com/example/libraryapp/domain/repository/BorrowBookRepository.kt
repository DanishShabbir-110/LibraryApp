package com.example.libraryapp.domain.repository

import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import kotlinx.coroutines.flow.Flow

interface BorrowBookRepository {
    fun getAllBorrowBooks(): Flow<List<BorrowBook>>
    fun getAllBorrowBooksByStatus(status: BorrowingStatus): Flow<List<BorrowBook>>

    suspend fun getBorrowBookById(id: Int): BorrowBook?

    suspend fun issueBook(book: BorrowBook)

    suspend fun returnBook(borrowingId: Int, returnDate: Long)

    suspend fun getActiveBorrowBook(bookId: Int, memberId: Int): BorrowBook?

    suspend fun updateBorrowBookName(bookName: String, bookId: Int)
    suspend fun updateBorrowMemberName(memberName: String, memberId: Int)

}