package com.example.libraryapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Member")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val name:String,
    val email:String,
    val phone:String
)
