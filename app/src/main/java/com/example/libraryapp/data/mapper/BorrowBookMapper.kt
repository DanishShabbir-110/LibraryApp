package com.example.libraryapp.data.mapper

import com.example.libraryapp.data.local.models.BorrowBookEntity
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus

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