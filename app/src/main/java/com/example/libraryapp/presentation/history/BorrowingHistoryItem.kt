package com.example.libraryapp.presentation.history

import com.example.libraryapp.domain.model.BorrowingStatus

data class BorrowingHistoryItem(
    val borrowingId: Int,
    val bookName: String,
    val memberName: String,
    val issueDate: Long,
    val returnDate: Long?,
    val status: BorrowingStatus
)
