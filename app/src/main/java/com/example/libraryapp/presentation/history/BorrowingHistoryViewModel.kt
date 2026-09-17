package com.example.libraryapp.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.repository.BorrowBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorrowingHistoryViewModel @Inject constructor(
    private val repository: BorrowBookRepository
) : ViewModel() {
    private var allBooks: List<BorrowingHistoryItem> = emptyList()
    private var _selectedStatus = MutableStateFlow(BorrowingStatus.ALL)
    val selectedStatus = _selectedStatus.asStateFlow()
    private var _borrowBooks = MutableStateFlow<List<BorrowingHistoryItem>>(emptyList())
    val borrowBooks = _borrowBooks.asStateFlow()
    val statusList = BorrowingStatus.entries
    private var _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        getBorrowBooks()
    }

    private fun getBorrowBooks() {
        viewModelScope.launch {
            repository.getAllBorrowBooks().collectLatest { borrowingHistory ->
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
                _borrowBooks.value = allBooks
                _isLoading.value = false
            }
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
        _borrowBooks.value = filteredBooks
    }

    fun selected(position: Int) {
        val selectedIndexedStatus = statusList[position]
        _selectedStatus.value = selectedIndexedStatus
        filterByStatus(selectedIndexedStatus)
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
        _borrowBooks.value = filteredList
    }
}