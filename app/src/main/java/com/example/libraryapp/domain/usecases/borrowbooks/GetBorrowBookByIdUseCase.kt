package com.example.libraryapp.domain.cases.borrowbooks

import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class GetBorrowBookByIdUseCase @Inject constructor(
    private val repository: BorrowBookRepository
) {
    suspend operator fun invoke(id: Int) = repository.getBorrowBookById(id)
}