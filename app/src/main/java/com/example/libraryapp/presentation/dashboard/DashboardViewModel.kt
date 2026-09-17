package com.example.libraryapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _totalBooks = MutableStateFlow(0)
    val totalBooks = _totalBooks.asStateFlow()
    private val _totalMembers = MutableStateFlow(0)
    val totalMembers = _totalMembers.asStateFlow()
    private val _issueBooks = MutableStateFlow(0)
    val issueBooks = _issueBooks.asStateFlow()
    private val _availableCopies = MutableStateFlow(0)
    val availableCopies = _availableCopies.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()


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
                _totalBooks.value = books.size
                _totalMembers.value = members.size
                _issueBooks.value = borrowBooks.count {
                    it.status == BorrowingStatus.ISSUED
                }
                _availableCopies.value = books.sumOf {
                    it.availableCopies
                }
                _isLoading.value = false
            }.collectLatest {}
        }
    }
}