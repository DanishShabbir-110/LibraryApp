package com.example.libraryapp.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.cases.borrowbooks.GetBorrowBooksUseCase
import com.example.libraryapp.domain.model.BorrowingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorrowingHistoryViewModel @Inject constructor(
    private val getBorrowBooksUseCase: GetBorrowBooksUseCase
) : ViewModel() {
    private var allBooks: List<BorrowingHistoryItem> = emptyList()
    private var _uiState = MutableStateFlow(BorrowingHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getBorrowBooks()
    }

    private fun getBorrowBooks() {
        viewModelScope.launch {
            getBorrowBooksUseCase().collectLatest { borrowingHistory ->
                allBooks = borrowingHistory.map { borrowBook ->
                    BorrowingHistoryItem(
                        borrowingId = borrowBook.id,
                        memberName = borrowBook.memberName,
                        bookName = borrowBook.bookName,
                        issueDate = borrowBook.issueDate,
                        returnDate = borrowBook.returnDate,
                        status = borrowBook.status
                    )
                }
                _uiState.update {
                    it.copy(
                        borrowList = allBooks, isLoading = false, errorMessage = null
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    fun searchBooks(query: String) {

        val filteredBooks = if (query.isBlank()) {
            allBooks
        } else {
            allBooks.filter { borrowingBook ->
                borrowingBook.bookName.contains(
                    query, ignoreCase = true
                ) || borrowingBook.memberName.contains(
                    query, ignoreCase = true
                )
            }
        }

        _uiState.update {
            it.copy(
                borrowList = filteredBooks
            )
        }
    }

    fun filterByStatus(filter: BorrowingStatus) {

        val filteredList = when (filter) {

            BorrowingStatus.ALL -> allBooks

            BorrowingStatus.ISSUED -> {
                allBooks.filter {
                    it.status == BorrowingStatus.ISSUED
                }
            }

            BorrowingStatus.RETURNED -> {
                allBooks.filter {
                    it.status == BorrowingStatus.RETURNED
                }
            }
        }

        _uiState.update {
            it.copy(
                borrowList = filteredList
            )
        }
    }
}