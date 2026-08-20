package com.example.libraryapp.di

import com.example.libraryapp.data.repository.BookRepositoryImpl
import com.example.libraryapp.data.repository.BorrowBookRepositoryImpl
import com.example.libraryapp.data.repository.MemberRepositoryImpl
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract  class RepositoryModule {

    @Binds
    abstract fun bindBookRepository(
        implementation: BookRepositoryImpl
    ): BookRepository

    @Binds
    abstract fun bindMemberRepository(
        implementation: MemberRepositoryImpl
    ): MemberRepository

    @Binds
    abstract fun bindBorrowBookRepository(
        implementation: BorrowBookRepositoryImpl
    ): BorrowBookRepository
}