package com.example.libraryapp.data.repository

import com.example.libraryapp.data.local.daos.BorrowBookDao
import com.example.libraryapp.data.mapper.toDomain
import com.example.libraryapp.data.mapper.toEntity
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.repository.BorrowBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BorrowBookRepositoryImpl @Inject constructor(
    private val borrowBookDao: BorrowBookDao
) : BorrowBookRepository {
    override fun getAllBorrowBooks(): Flow<List<BorrowBook>> {
        return borrowBookDao.getAllBorrowBooks().map { borrowBookEntities ->
            borrowBookEntities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override fun getAllBorrowBooksByStatus(status: BorrowingStatus): Flow<List<BorrowBook>> {
        return borrowBookDao.getAllBorrowBooksByStatus(status).map { borrowBookEntities ->
            borrowBookEntities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getBorrowBookById(id: Int): BorrowBook? {
        return borrowBookDao.getBorrowBookById(id)?.toDomain()
    }

    override suspend fun issueBook(book: BorrowBook) {
        borrowBookDao.issueBook(book.toEntity())
    }

    override suspend fun returnBook(borrowingId: Int, returnDate: Long) {
        borrowBookDao.returnBook(borrowingId, returnDate)
    }

    override suspend fun getActiveBorrowBook(
        bookId: Int,
        memberId: Int
    ): BorrowBook? {
        return borrowBookDao.getActiveBorrowBook(bookId,memberId)?.toDomain()
    }

    override suspend fun updateBorrowBookName(bookName: String, bookId: Int) {
        borrowBookDao.updateBorrowBookName(bookName,bookId)
    }

    override suspend fun updateBorrowMemberName(memberName: String, memberId: Int) {
        borrowBookDao.updateBorrowMemberName(memberName,memberId)
    }

}