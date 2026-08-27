package com.example.libraryapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.libraryapp.data.local.daos.BooksDao
import com.example.libraryapp.data.local.daos.BorrowBookDao
import com.example.libraryapp.data.local.daos.MembersDao
import com.example.libraryapp.data.local.entities.BookEntity
import com.example.libraryapp.data.local.entities.BorrowBookEntity
import com.example.libraryapp.data.local.entities.MemberEntity

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