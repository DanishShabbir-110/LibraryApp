package com.example.libraryapp.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.libraryapp.data.local.models.MemberEntity

@Dao
interface MembersDao {

    @Query("SELECT * FROM Member")
    fun getAllMembers(): List<MemberEntity>
}