package com.example.libraryapp.di

import android.content.Context
import androidx.room.Room
import com.example.libraryapp.data.local.LibraryDatabase
import com.example.libraryapp.data.local.daos.BooksDao
import com.example.libraryapp.data.local.daos.BorrowBookDao
import com.example.libraryapp.data.local.daos.MembersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LibraryDatabase {
        return Room.databaseBuilder(
            context,
            LibraryDatabase::class.java,
            "Library Database"
        ).build()
    }

    @Provides
    fun bookDao(
        database: LibraryDatabase
    ): BooksDao {
        return database.getBookDao()
    }

    @Provides
    fun memberDao(
        database: LibraryDatabase
    ): MembersDao {
        return database.getMemberDao()
    }
    @Provides
    fun BorrowBookDao(
        database: LibraryDatabase
    ): BorrowBookDao {
        return database.getBorrowBookDao()
    }
}