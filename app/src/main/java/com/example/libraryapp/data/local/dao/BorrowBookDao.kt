package com.example.libraryapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.libraryapp.data.local.entity.BorrowBookEntity
import com.example.libraryapp.domain.model.BorrowingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowBookDao {

@Query("SELECT * FROM borrow_book_table")
    fun getAllBorrowBooks(): Flow<List<BorrowBookEntity>>

    @Query("SELECT * FROM borrow_book_table where status=:status")
    fun getAllBorrowBooksByStatus(
        status: BorrowingStatus
    ): Flow<List<BorrowBookEntity>>

    @Query("SELECT * FROM borrow_book_table where borrow_book_id=:id")
    suspend fun getBorrowBookById(id: Int): BorrowBookEntity?

    @Insert
    suspend fun issueBook(book: BorrowBookEntity)

    @Query("""
        UPDATE borrow_book_table
        SET status = 'RETURNED',
            return_date = :returnDate
        WHERE borrow_book_id = :borrowingId
    """)
    suspend fun returnBook(
        borrowingId: Int,
        returnDate: Long
    )

    @Query("""
        SELECT * FROM borrow_book_table
        WHERE book_id = :bookId
        AND member_id = :memberId
        AND status = :status
        LIMIT 1
    """)
    suspend fun getActiveBorrowBook(
        bookId: Int,
        memberId: Int,
        status: BorrowingStatus = BorrowingStatus.ISSUED
    ): BorrowBookEntity?


    @Query("Update borrow_book_table set book_name=:bookName where book_id=:bookId")
    suspend fun updateBorrowBookName(bookName:String, bookId:Int)

    @Query("Update borrow_book_table set member_name=:memberName where member_id=:memberId")
    suspend fun updateBorrowMemberName(memberName:String,memberId:Int)


}