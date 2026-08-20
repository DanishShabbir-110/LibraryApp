package com.example.libraryapp.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.libraryapp.data.local.models.BookEntity
import com.example.libraryapp.domain.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BooksDao {
    @Query("SELECT * FROM Book")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM Book WHERE id = :id")
    suspend fun getBookById(id: Int): BookEntity?

    @Insert
    suspend fun addBook(book: BookEntity)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("""
    UPDATE Book
    SET availableCopies = availableCopies - 1
    WHERE id = :bookId
    AND availableCopies > 0
""")
    suspend fun decreaseAvailableCopies(
        bookId: Int
    )

    @Query("""
    UPDATE Book
    SET availableCopies = availableCopies + 1
    WHERE id = :bookId
""")
    suspend fun increaseAvailableCopies(
        bookId: Int
    )
}