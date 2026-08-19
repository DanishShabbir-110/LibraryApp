package com.example.libraryapp.domain.repository

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    fun getAllMembers(): Flow<List<Member>>

    suspend fun getMemberById(id:Int): Member?

    suspend fun addMember(book: Member)
    suspend fun updateMember(book: Member)
    suspend fun deleteMember(book: Member)
}