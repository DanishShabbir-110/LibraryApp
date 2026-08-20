package com.example.libraryapp.domain.usecases.books

import com.example.libraryapp.domain.repository.BookRepository
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(id:Int)=repository.getBookById(id)
}