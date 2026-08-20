package com.example.libraryapp.domain.usecases.members

import com.example.libraryapp.domain.repository.MemberRepository
import javax.inject.Inject

class GetMemberByIdUseCase @Inject constructor(
    private val repository: MemberRepository
) {
    suspend operator fun invoke(id:Int)=repository.getMemberById(id)
}