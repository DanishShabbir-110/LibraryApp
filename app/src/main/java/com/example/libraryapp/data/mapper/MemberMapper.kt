package com.example.libraryapp.data.mapper

import com.example.libraryapp.data.local.models.MemberEntity
import com.example.libraryapp.domain.model.Member

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