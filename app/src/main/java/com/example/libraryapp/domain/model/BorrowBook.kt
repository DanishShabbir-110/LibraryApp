package com.example.libraryapp.domain.model

data class BorrowBook(
    val id: Int = 0,
    val bookId: Int,
    val memberId: Int,
    val issueDate: Long,
    val expectedReturnDate: Long,
    val actualReturnDate: Long,
    val status: BorrowingStatus
)
