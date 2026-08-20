package com.example.libraryapp.domain.usecases.members

import com.example.libraryapp.domain.repository.MemberRepository
import javax.inject.Inject

class GetMembersUseCase@Inject constructor(
    private val repository: MemberRepository
) {
    operator fun invoke()=repository.getAllMembers()
}