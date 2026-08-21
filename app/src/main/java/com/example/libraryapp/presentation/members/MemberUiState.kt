package com.example.libraryapp.presentation.members

import com.example.libraryapp.domain.model.Member

data class MemberUiState(
    val members: List<Member> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
