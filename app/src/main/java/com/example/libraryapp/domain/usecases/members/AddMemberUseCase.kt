package com.example.libraryapp.domain.usecases.members

import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.MemberRepository
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(member: Member)=repository.addMember(member)
}