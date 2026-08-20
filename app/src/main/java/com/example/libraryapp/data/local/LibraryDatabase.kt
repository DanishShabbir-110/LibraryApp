package com.example.libraryapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.libraryapp.data.local.daos.BooksDao
import com.example.libraryapp.data.local.daos.BorrowBookDao
import com.example.libraryapp.data.local.daos.MembersDao
import com.example.libraryapp.data.local.models.BookEntity
import com.example.libraryapp.data.local.models.BorrowBookEntity
import com.example.libraryapp.data.local.models.MemberEntity

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

    companion object {
        private var INSTANCE: LibraryDatabase?=null
        fun getDatabase(context: Context): LibraryDatabase{
            return INSTANCE?:synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "Library Database"
                ).build()
                INSTANCE=instance
                instance
            }
        }
    }

}