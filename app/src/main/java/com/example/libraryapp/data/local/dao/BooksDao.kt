package com.example.libraryapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.libraryapp.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BooksDao {

    @Query("SELECT * FROM book_table")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE book_id = :id")
    suspend fun getBookById(id: Int): BookEntity?

    @Insert
    suspend fun addBook(book: BookEntity)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("UPDATE book_table SET book_available_copies = book_available_copies - 1 WHERE book_id = :bookId AND book_available_copies > 0")
    suspend fun decreaseAvailableCopies(bookId: Int)

    @Query("UPDATE book_table SET book_available_copies = book_available_copies + 1 WHERE book_id = :bookId ")
    suspend fun increaseAvailableCopies(bookId: Int)
}