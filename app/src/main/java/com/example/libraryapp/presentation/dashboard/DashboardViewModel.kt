package com.example.libraryapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val memberRepository: MemberRepository,
    private val borrowBookRepository: BorrowBookRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                bookRepository.getAllBooks(),
                memberRepository.getAllMembers(),
                borrowBookRepository.getAllBorrowBooks()
            ) { books, members, borrowBooks ->
                DashboardUiState(
                    totalBooks = books.size,
                    totalMembers = members.size,
                    issuedBooks = borrowBooks.count {
                        it.status == BorrowingStatus.ISSUED
                    },
                    availableBooks = books.sumOf {
                        it.availableCopies
                    },
                    isLoading = false
                )
            }.collectLatest { dashboardUiState ->
                _uiState.value = dashboardUiState

            }
        }
    }
}

data class DashboardUiState(
    val totalBooks: Int = 0,
    val totalMembers: Int = 0,
    val issuedBooks: Int = 0,
    val availableBooks: Int = 0,
    val isLoading: Boolean = true
)