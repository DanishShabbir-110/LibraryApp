package com.example.libraryapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity("Member")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val name:String,
    val email:String,
    val phone:String
): Serializable
