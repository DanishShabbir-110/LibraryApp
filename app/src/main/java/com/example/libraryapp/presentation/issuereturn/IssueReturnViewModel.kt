package com.example.libraryapp.presentation.issuereturn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
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
    private val bookRepository: BookRepository,
    private val memberRepository: MemberRepository,
    private val borrowBookRepository: BorrowBookRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(IssueReturnUiState())
    val uiState: StateFlow<IssueReturnUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {

        viewModelScope.launch {

            combine(
                bookRepository.getAllBooks(), memberRepository.getAllMembers()
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
                val getBorrowBook = borrowBookRepository.getActiveBorrowBook(book.id, member.id)
                if (getBorrowBook != null) {
                    throw IllegalStateException("Book Already issued to that person.")
                }
                val issueBook = BorrowBook(
                    bookId = book.id,
                    memberId = member.id,
                    issueDate = issueDate,
                    memberName = member.name,
                    bookName = book.name,
                    returnDate = null,
                    status = BorrowingStatus.ISSUED
                )
                borrowBookRepository.issueBook(issueBook)
                bookRepository.decreaseAvailableCopies(book.id)
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
                val getBorrowBook = borrowBookRepository.getActiveBorrowBook(book.id, member.id)
                    ?: throw IllegalStateException("No active borrowing found for this book and member")
                borrowBookRepository.returnBook(getBorrowBook.id, returnDate)
                bookRepository.increaseAvailableCopies(book.id)

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

data class IssueReturnUiState(
    val members: List<Member> = emptyList(),
    val books: List<Book> = emptyList(),
    val selectedMember: Member? = null,
    val selectedBook: Book? = null,
    val issueDate: Long? = null,
    val returnDate: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
