package com.example.libraryapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus

@Entity("borrow_book_table")
data class BorrowBookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("borrow_book_id")
    val id:Int = 0,
    @ColumnInfo("book_id")
    val bookId:Int,
    @ColumnInfo("member_id")
    val memberId:Int,
    @ColumnInfo("book_name")
    val bookName: String,
    @ColumnInfo("member_name")
    val memberName: String,
    @ColumnInfo("issue_date")
    val issueDate:Long,
    @ColumnInfo("return_date")
    val returnDate:Long?=null,
    @ColumnInfo("status")
    val status:String
)


fun BorrowBookEntity.toDomain(): BorrowBook {
    return BorrowBook(
        id=id,
        bookId = bookId,
        memberId = memberId,
        issueDate = issueDate,
        returnDate = returnDate,
        memberName = memberName,
        bookName = bookName,
        status = when (status) {
            "RETURNED" -> BorrowingStatus.RETURNED
            else -> BorrowingStatus.ISSUED
        }
    )
}

fun BorrowBook.toEntity(): BorrowBookEntity {
    return BorrowBookEntity(
        id=id,
        bookId = bookId,
        memberId = memberId,
        memberName = memberName,
        bookName = bookName,
        issueDate = issueDate,
        returnDate = returnDate,
        status = status.name
    )
}
