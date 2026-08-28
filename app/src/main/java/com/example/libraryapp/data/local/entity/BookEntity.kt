package com.example.libraryapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BookCategory

@Entity(tableName = "book_table")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("book_id")
    val id: Int = 0,
    @ColumnInfo("book_name")
    val name: String,
    @ColumnInfo("book_author")
    val author: String,
    @ColumnInfo("book_category")
    val category: String,
    @ColumnInfo("book_copies")
    val totalCopies: Int,
    @ColumnInfo("book_available_copies")
    val availableCopies: Int
)

fun BookEntity.toDomain(): Book {
    return Book(
        id = id,
        name = name,
        author = author,
        category = BookCategory.entries.find {
            it.name.equals(category, ignoreCase = true)
        } ?: BookCategory.PROGRAMMING,
        totalCopies = totalCopies,
        availableCopies = availableCopies
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        name = name,
        author = author,
        category = category.name.lowercase().replaceFirstChar { char -> char.uppercase() },
        totalCopies = totalCopies,
        availableCopies = availableCopies
    )
}
