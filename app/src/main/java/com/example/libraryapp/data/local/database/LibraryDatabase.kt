package com.example.libraryapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.libraryapp.data.local.dao.BooksDao
import com.example.libraryapp.data.local.dao.BorrowBookDao
import com.example.libraryapp.data.local.dao.MembersDao
import com.example.libraryapp.data.local.entity.BookEntity
import com.example.libraryapp.data.local.entity.BorrowBookEntity
import com.example.libraryapp.data.local.entity.MemberEntity

@Database(
    entities = [
        BookEntity::class,
        MemberEntity::class,
        BorrowBookEntity::class
    ],
    exportSchema = false,
    version = 1
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun getBookDao(): BooksDao
    abstract fun getMemberDao(): MembersDao
    abstract fun getBorrowBookDao(): BorrowBookDao
}