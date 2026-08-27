package com.example.libraryapp.presentation.issue_return

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.cases.borrowbooks.IssueBookUseCase
import com.example.libraryapp.domain.cases.borrowbooks.ReturnBookUseCase
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.usecases.books.GetBooksUseCase
import com.example.libraryapp.domain.usecases.members.GetMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class IssueReturnViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val issueBookUseCase: IssueBookUseCase,
    private val returnBookUseCase: ReturnBookUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(IssueReturnUiState())
    val uiState: StateFlow<IssueReturnUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {

        viewModelScope.launch {

            combine(
                getBooksUseCase(), getMembersUseCase()
            ) { books, members ->
                books to members
            }.collectLatest { (books, members) ->

                _uiState.update {
                    it.copy(
                        books = books.filter { filter ->
                            filter.availableCopies > 0
                        }, members = members
                    )
                }
            }
        }
    }

    fun selectMember(member: Member) {
        _uiState.update {
            it.copy(
                selectedMember = member, errorMessage = null
            )
        }
    }

    fun selectBook(book: Book) {
        _uiState.update {
            it.copy(
                selectedBook = book, errorMessage = null
            )
        }
    }

    fun selectIssueDate(date: Long) {
        _uiState.update {
            it.copy(
                issueDate = date
            )
        }
    }

    fun selectReturnDate(date: Long) {
        _uiState.update {
            it.copy(
                returnDate = date
            )
        }
    }

    fun issueBook() {
        val state = _uiState.value

        val member = state.selectedMember
        val book = state.selectedBook
        val issueDate = state.issueDate

        if (member == null) {
            showError("Please Select a member.")
            return
        }
        if (book == null) {
            showError("Please Select a book.")
            return
        }
        if (issueDate == null) {
            showError("Please Select Issue Date.")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true, errorMessage = null
                    )
                }

                issueBookUseCase(
                    book, member, issueDate
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Issue Book Successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = e.message ?: "Failed to issue a book."
                    )
                }
            }
        }
    }

    fun returnBook() {
        val state = _uiState.value

        val member = state.selectedMember
        val book = state.selectedBook
        val returnDate = state.returnDate

        if (member == null) {
            showError("Please Select a member.")
            return
        }
        if (book == null) {
            showError("Please Select a book.")
            return
        }
        if (returnDate == null) {
            showError("Please Select Return Date.")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true, errorMessage = null
                    )
                }

                returnBookUseCase(
                    bookId = book.id, memberId = member.id, returnDate = returnDate
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Return Book Successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = e.message ?: "Failed to Return a book."
                    )
                }
            }
        }
    }

    private fun showError(message: String) {

        _uiState.update {
            it.copy(
                errorMessage = message
            )
        }
    }

    fun clearError() {

        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    fun clearSuccess() {

        _uiState.update {
            it.copy(
                isSuccess = false, successMessage = null
            )
        }
    }

    fun clearForm() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedBook = null,
                selectedMember = null,
                issueDate = null,
                returnDate = null,
                isLoading = false,
                isSuccess = false,
                successMessage = null,
                errorMessage = null
            )
        }
    }
}