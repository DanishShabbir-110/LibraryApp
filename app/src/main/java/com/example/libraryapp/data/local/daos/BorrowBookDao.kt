package com.example.libraryapp.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.libraryapp.data.local.entities.BorrowBookEntity
import com.example.libraryapp.domain.model.BorrowingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowBookDao {

@Query("SELECT * FROM BorrowBooks")
    fun getAllBorrowBooks(): Flow<List<BorrowBookEntity>>

    @Query("SELECT * FROM BorrowBooks where status=:status")
    fun getAllBorrowBooksByStatus(
        status: BorrowingStatus
    ): Flow<List<BorrowBookEntity>>

    @Query("SELECT * FROM BorrowBooks where id=:id")
    suspend fun getBorrowBookById(id: Int): BorrowBookEntity?

    @Insert
    suspend fun issueBook(book: BorrowBookEntity)

    @Query("""
        UPDATE BorrowBooks
        SET status = 'RETURNED',
            returnDate = :returnDate
        WHERE id = :borrowingId
    """)
    suspend fun returnBook(
        borrowingId: Int,
        returnDate: Long
    )

    @Query("""
        SELECT * FROM BorrowBooks
        WHERE bookId = :bookId
        AND memberId = :memberId
        AND status = :status
        LIMIT 1
    """)
    suspend fun getActiveBorrowBook(
        bookId: Int,
        memberId: Int,
        status: BorrowingStatus = BorrowingStatus.ISSUED
    ): BorrowBookEntity?


    @Query("Update BorrowBooks set bookName=:bookName where bookId=:bookId")
    suspend fun updateBorrowBookName(bookName:String,bookId:Int)

    @Query("Update BorrowBooks set memberName=:memberName where memberId=:memberId")
    suspend fun updateBorrowMemberName(memberName:String,memberId:Int)


}