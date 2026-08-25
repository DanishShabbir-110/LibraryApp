package com.example.libraryapp.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.libraryapp.data.local.entities.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MembersDao {

    @Query("SELECT * FROM Member")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM Member WHERE id=:id")
    suspend fun getMemberById(id: Int): MemberEntity?

    @Insert
    suspend fun addMember(member: MemberEntity)
    @Update
    suspend fun updateMember(member: MemberEntity)
    @Delete
    suspend fun deleteMember(member: MemberEntity)
}