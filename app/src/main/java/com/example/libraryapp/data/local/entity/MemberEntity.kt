package com.example.libraryapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.libraryapp.domain.model.Member

@Entity("member_table")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("member_id")
    val id:Int=0,
    @ColumnInfo("member_name")
    val name:String,
    @ColumnInfo("member_email")
    val email:String,
    @ColumnInfo("member_phone")
    val phone:String
)

fun MemberEntity.toDomain(): Member {
    return Member(
        id=id,
        name = name,
        email=email,
        phone=phone
    )
}

fun Member.toEntity(): MemberEntity {
    return MemberEntity(
        id=id,
        name = name,
        email=email,
        phone=phone
    )
}
