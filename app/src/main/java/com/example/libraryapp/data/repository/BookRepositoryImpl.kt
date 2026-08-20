package com.example.libraryapp.data.repository

import com.example.libraryapp.data.local.daos.BooksDao
import com.example.libraryapp.data.mapper.toDomain
import com.example.libraryapp.data.mapper.toEntity
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BooksDao
) : BookRepository {
    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { bookEntities ->
            bookEntities.map { bookEntity ->
                bookEntity.toDomain()
            }
        }
    }

    override suspend fun getBookById(id: Int): Book? {
        return bookDao.getBookById(id)?.toDomain()
    }

    override suspend fun addBook(book: Book) {
        bookDao.addBook(book.toEntity())
    }

    override suspend fun updateBook(book: Book) {
        bookDao.updateBook(book.toEntity())
    }

    override suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book.toEntity())
    }

    override suspend fun decreaseAvailableCopies(bookId: Int) {
        bookDao.decreaseAvailableCopies(bookId)
    }

    override suspend fun increaseAvailableCopies(bookId: Int) {
        bookDao.increaseAvailableCopies(bookId)
    }
}