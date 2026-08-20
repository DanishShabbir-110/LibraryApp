package com.example.libraryapp.presentation.issue_return

import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.Member

data class IssueReturnUiState(
    val members: List<Member> = emptyList(),
    val books: List<Book> = emptyList(),

    val selectedMember: Member? = null,
    val selectedBook: Book? = null,

    val issueDate: Long? = null,
    val returnDate: Long? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
