package com.example.libraryapp.domain.cases.borrowbooks

import com.example.libraryapp.domain.repository.BorrowBookRepository
import javax.inject.Inject

class GetBorrowBooksUseCase @Inject constructor(
    private val repository: BorrowBookRepository
) {
    operator fun invoke()=repository.getAllBorrowBooks()
}