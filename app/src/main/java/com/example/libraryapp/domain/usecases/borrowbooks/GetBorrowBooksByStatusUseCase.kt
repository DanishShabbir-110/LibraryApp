package com.example.libraryapp.domain.cases.borrowbooks

import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class GetBorrowBooksByStatusUseCase @Inject constructor(
    private val repository: BorrowBookRepository
) {
    operator fun invoke(status: BorrowingStatus)=repository.getAllBorrowBooksByStatus(status)
}