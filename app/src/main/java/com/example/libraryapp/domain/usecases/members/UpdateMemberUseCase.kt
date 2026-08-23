package com.example.libraryapp.domain.usecases.members

import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
import javax.inject.Inject

class UpdateMemberUseCase @Inject constructor(
    private val repository: MemberRepository,
    private val borrowBookRepository: BorrowBookRepository
) {
    suspend operator fun invoke(member: Member) {
        repository.updateMember(member)
        borrowBookRepository.updateBorrowMemberName(member.name,member.id)
    }
}