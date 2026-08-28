package com.example.libraryapp.data.repository

import com.example.libraryapp.data.local.dao.MembersDao
import com.example.libraryapp.data.local.entity.toDomain
import com.example.libraryapp.data.local.entity.toEntity
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val membersDao: MembersDao
): MemberRepository {
    override fun getAllMembers(): Flow<List<Member>> {
        return membersDao.getAllMembers().map {memberEntities ->
            memberEntities.map {entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getMemberById(id: Int): Member? {
        return membersDao.getMemberById(id)?.toDomain()
    }

    override suspend fun addMember(member: Member) {
        membersDao.addMember(member.toEntity())
    }

    override suspend fun updateMember(member: Member) {
        membersDao.updateMember(member.toEntity())
    }

    override suspend fun deleteMember(member: Member) {
        membersDao.deleteMember(member.toEntity())
    }
}