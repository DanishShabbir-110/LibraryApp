package com.example.libraryapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.libraryapp.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MembersDao {

    @Query("SELECT * FROM member_table")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM member_table WHERE member_id=:id")
    suspend fun getMemberById(id: Int): MemberEntity?

    @Insert
    suspend fun addMember(member: MemberEntity)
    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)
}