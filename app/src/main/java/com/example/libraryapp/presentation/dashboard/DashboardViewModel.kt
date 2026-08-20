package com.example.libraryapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.cases.borrowbooks.GetBorrowBooksUseCase
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.usecases.books.GetBooksUseCase
import com.example.libraryapp.domain.usecases.members.GetMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val getBorrowBooksUseCase: GetBorrowBooksUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                getBooksUseCase(),
                getMembersUseCase(),
                getBorrowBooksUseCase()
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
            }.collect { dashboardUiState ->
                _uiState.value = dashboardUiState

            }
        }
    }
}