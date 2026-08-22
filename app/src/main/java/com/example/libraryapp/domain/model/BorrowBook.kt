package com.example.libraryapp.domain.model

data class BorrowBook(
    val id: Int = 0,
    val bookId: Int,
    val memberId: Int,
    val bookName: String,
    val memberName: String,
    val issueDate: Long,
    val returnDate: Long?=null,
    val status: BorrowingStatus
)
